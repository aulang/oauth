package cn.aulang.oauth.controller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import cn.aulang.oauth.model.AccessToken;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.security.auth.login.AccountLockedException;
import java.util.List;
import java.util.Objects;

/**
 * @author wulang
 */
@Controller
public class OAuthController {

    private final AuthCodeBiz codeBiz;
    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final AccountTokenBiz tokenBiz;
    private final AuthRequestBiz requestBiz;
    private final ReturnPageBiz returnPageBiz;

    @Autowired
    public OAuthController(AuthCodeBiz codeBiz, ClientBiz clientBiz, AccountBiz accountBiz,
                           AccountTokenBiz tokenBiz, AuthRequestBiz requestBiz, ReturnPageBiz returnPageBiz) {
        this.codeBiz = codeBiz;
        this.clientBiz = clientBiz;
        this.accountBiz = accountBiz;
        this.tokenBiz = tokenBiz;
        this.requestBiz = requestBiz;
        this.returnPageBiz = returnPageBiz;
    }

    /**
     * 登录认证请求
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam(name = "client_id") String clientId,
                            @RequestParam(name = "response_type") String responseType,
                            @RequestParam(name = "redirect_uri") String redirectUri,
                            @RequestParam(name = "state", required = false) String state,
                            @RequestParam(name = "code_challenge", required = false) String codeChallenge,
                            @CookieValue(name = Constants.SSO_COOKIE_NAME, required = false) String authorizeId,
                            HttpServletResponse response, Model model) {
        Client client = clientBiz.get(clientId);
        if (client == null) {
            // clientId不存在
            return Constants.errorPage(model, "无效的client_id");
        }

        String authGrant = OAuthConstants.AuthorizationGrant.typeOf(responseType);
        if (authGrant == null) {
            return Constants.errorPage(model, "无效的response_type");
        }

        if (!client.getAuthorizationGrants().contains(authGrant)) {
            return Constants.errorPage(model, "未授权的response_type");
        }

        List<String> registeredUrls = client.getRegisteredUris();
        if (redirectUri != null) {
            boolean result = registeredUrls.parallelStream().anyMatch(url -> StrUtil.startWith(redirectUri, url, true));
            if (!result) {
                return Constants.errorPage(model, "未注册的redirect_uri");
            }
        }

        AuthRequest request = null;
        if (authorizeId != null) {
            request = requestBiz.get(authorizeId);
            if (request != null) {
                if (!Objects.equals(clientId, request.getClientId())
                        || !Objects.equals(authGrant, request.getAuthGrant())
                        || !Objects.equals(redirectUri, request.getRedirectUri())
                        || !Objects.equals(codeChallenge, request.getCodeChallenge())
                        || !Objects.equals(state, request.getState())) {

                    request.setClientId(clientId);
                    request.setAuthGrant(authGrant);
                    request.setRedirectUri(redirectUri);
                    request.setCodeChallenge(codeChallenge);
                    request.setState(state);
                    requestBiz.save(request);
                }

                if (request.getAuthenticated()) {
                    if (request.getMustChpwd() != null && request.getMustChpwd()) {
                        return returnPageBiz.changePwdPage(request, model);
                    }

                    return returnPageBiz.redirect(request, model);
                }
            }
        }

        if (request == null) {
            request = requestBiz.createAndSave(clientId, authGrant, redirectUri, codeChallenge, state);
        }

        Constants.setSsoCookie(response, request.getId());
        return returnPageBiz.loginPage(request, client, model);
    }

    /**
     * 授权码模式、密码模式、刷新令牌和凭证模式发放access_token，
     * 简化模式不在此发放access_token
     *
     * @param clientId     客户端的ID，必选项
     * @param grantType    使用的授权模式，必选项
     *                     授权码模式为"authorization_code",
     *                     密码模式为"password",
     *                     刷新令牌为"refresh_token",
     *                     凭证模式为"client_credentials"
     * @param code         授权码模式获得的授权码，授权码模式必选项
     * @param clientSecret 客户端凭证，授权码模式和凭证模式必选项
     * @param redirectUri  重定向URI，授权码模式可选项
     * @param username     用户名，密码模式必选项
     * @param password     用户密码SHA256摘要，密码模式必选项
     * @param refreshToken 刷新access_token
     */
    @ResponseBody
    @PostMapping(path = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> token(@RequestParam(name = "client_id") String clientId,
                                   @RequestParam(name = "grant_type") String grantType,

                                   @RequestParam(name = "code", required = false) String code,
                                   @RequestParam(name = "client_secret", required = false) String clientSecret,
                                   @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                                   @RequestParam(name = "code_verifier", required = false) String codeVerifier,

                                   @RequestParam(name = "username", required = false) String username,
                                   @RequestParam(name = "password", required = false) String password,

                                   @RequestParam(name = "refresh_token", required = false) String refreshToken) {
        Client client = clientBiz.get(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的客户端"));
        }

        if (!client.getAuthorizationGrants().contains(grantType)) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "未授权的grantType"));
        }

        switch (grantType.toLowerCase()) {
            case OAuthConstants.AuthorizationGrant.PASSWORD -> {
                // 密码模式
                if (StrUtil.hasBlank(username, password)) {
                    return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "账号和密码不能为空"));
                }

                try {
                    String accountId = accountBiz.login(username, password);
                    if (accountId != null) {
                        AccountToken accountToken = tokenBiz.create(clientId, grantType, accountId);

                        AccessToken accessToken = AccessToken.create(accountToken.getAccessToken(),
                                accountToken.getRefreshToken(), client.getAccessTokenExpiresIn());

                        return ResponseEntity.ok(accessToken);
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "账号或密码错误"));
                    }
                } catch (AccountLockedException e) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(Constants.error(HttpStatus.NOT_ACCEPTABLE.value(), "账号被锁定，请申诉解锁"));
                } catch (PasswordExpiredException e) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                            .body(Constants.error(HttpStatus.NOT_ACCEPTABLE.value(), "密码过期，必须修改密码"));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "账号或密码错误"));
                }
            }
            case OAuthConstants.AuthorizationGrant.AUTHORIZATION_CODE -> {
                // 授权码模式
                if (StrUtil.isBlank(code)) {
                    return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "code不能为空"));
                }

                AuthCode authCode = codeBiz.consumeCode(code);
                if (authCode == null) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "无效的code"));
                }

                if (!authCode.getRedirectUri().equalsIgnoreCase(redirectUri)) {
                    return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "redirect_uri不匹配"));
                }

                if (StrUtil.isNotBlank(authCode.getCodeChallenge())) {
                    // codeVerifier验证
                    if (StrUtil.isBlank(codeVerifier)) {
                        return ResponseEntity.badRequest()
                                .body(Constants.error(HttpStatus.BAD_REQUEST.value(), "code_verifier不能为空"));
                    }

                    String codeChallenge = Base64.encodeUrlSafe(DigestUtil.sha256(codeVerifier));
                    if (!codeChallenge.equals(authCode.getCodeChallenge())) {
                        return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "code_verifier错误"));
                    }
                } else {
                    // clientSecret验证
                    if (StrUtil.isBlank(clientSecret)) {
                        return ResponseEntity.badRequest()
                                .body(Constants.error(HttpStatus.BAD_REQUEST.value(), "clientSecret不能为空"));
                    }

                    if (!client.getSecret().equals(clientSecret)) {
                        return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "client_secret错误"));
                    }
                }

                AccountToken accountToken = tokenBiz.createByCode(authCode);

                AccessToken accessToken = AccessToken.create(accountToken.getAccessToken(),
                        accountToken.getRefreshToken(), client.getAccessTokenExpiresIn());

                return ResponseEntity.ok(accessToken);
            }
            case OAuthConstants.AuthorizationGrant.REFRESH_TOKEN -> {
                // 刷新access_token
                if (StrUtil.isBlank(refreshToken)) {
                    return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "refresh_token不能为空"));
                }

                AccountToken accountToken = tokenBiz.refreshAccessToken(refreshToken);
                if (accountToken != null) {
                    AccessToken accessToken = AccessToken.create(accountToken.getAccessToken(),
                            accountToken.getRefreshToken(), client.getAccessTokenExpiresIn());

                    return ResponseEntity.ok(accessToken);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body(Constants.error(HttpStatus.UNAUTHORIZED.value(), "无效的refresh_token"));
                }
            }
            case OAuthConstants.AuthorizationGrant.CLIENT_CREDENTIALS -> {
                // 凭证式
                if (!client.getSecret().equals(clientSecret)) {
                    return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "client_secret错误"));
                }

                AccountToken accountToken = tokenBiz.create(clientId, grantType, "N/A");

                AccessToken accessToken = AccessToken.create(accountToken.getAccessToken(),
                        accountToken.getRefreshToken(), client.getAccessTokenExpiresIn());

                return ResponseEntity.ok(accessToken);
            }
            default -> {
                return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的grantType"));
            }
        }
    }
}
