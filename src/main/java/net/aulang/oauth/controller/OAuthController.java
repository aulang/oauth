package net.aulang.oauth.controller;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import net.aulang.oauth.common.Constants;
import net.aulang.oauth.common.OAuthConstants;
import net.aulang.oauth.entity.AccountToken;
import net.aulang.oauth.entity.AuthCode;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.exception.PasswordExpiredException;
import net.aulang.oauth.manage.AccountBiz;
import net.aulang.oauth.manage.AccountTokenBiz;
import net.aulang.oauth.manage.ApprovedScopeBiz;
import net.aulang.oauth.manage.AuthCodeBiz;
import net.aulang.oauth.manage.AuthRequestBiz;
import net.aulang.oauth.manage.ClientBiz;
import net.aulang.oauth.manage.ReturnPageBiz;
import net.aulang.oauth.model.AccessToken;
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
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:39
 */
@Controller
public class OAuthController {
    @Autowired
    private AuthCodeBiz codeBiz;
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AccountTokenBiz tokenBiz;
    @Autowired
    private AuthRequestBiz requestBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;
    @Autowired
    private ApprovedScopeBiz approvedScopeBiz;

    /**
     * 登录认证请求
     */
    @GetMapping("/authorize")
    public String authorize(@RequestParam(name = "client_id") String clientId,
                            @RequestParam(name = "response_type") String responseType,
                            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
                            @RequestParam(name = "scope", required = false) String scope,
                            @RequestParam(name = "state", required = false) String state,
                            @CookieValue(name = Constants.SSO_COOKIE_NAME, required = false) String ssoCookie,
                            Model model) {
        Client client = clientBiz.findOne(clientId);
        if (client == null) {
            /**
             * clientId不存在
             */
            return Constants.errorPage(model, "无效的client_id");
        }

        String authorizationGrant = OAuthConstants.AuthorizationGrant.typeOf(responseType);
        if (authorizationGrant == null) {
            return Constants.errorPage(model, "无效的response_type");
        }

        if (!client.getAuthorizationGrants().contains(authorizationGrant)) {
            return Constants.errorPage(model, "未授权的response_type");
        }

        String registeredUri = redirectUri;
        Set<String> registeredUrls = client.getRegisteredRedirectUris();
        if (redirectUri != null) {
            boolean result = registeredUrls.parallelStream().anyMatch(url -> {
                Pattern pattern = Pattern.compile(url, Pattern.CASE_INSENSITIVE);
                return pattern.matcher(redirectUri).matches();
            });
            if (!result) {
                return Constants.errorPage(model, "未注册的redirect_uri");
            }
        } else {
            if (registeredUrls.size() == 1) {
                registeredUri = registeredUrls.iterator().next();
            } else {
                return Constants.errorPage(model, "缺失redirect_uri");
            }
        }

        Set<String> scopes = new HashSet<>();
        if (scope != null) {
            List<String> requestScopes = Arrays.asList(scope.split(","));
            if (!client.getScopes().keySet().containsAll(requestScopes)) {
                return Constants.errorPage(model, "无效的scope");
            }
            scopes.addAll(requestScopes);
        } else {
            scopes.addAll(client.getAutoApprovedScopes());
        }

        if (ssoCookie != null) {
            /**
             * 单点登录
             */
            String accessToken = Base64.decodeStr(ssoCookie);
            AccountToken accountToken = tokenBiz.findByAccessToken(accessToken);
            if (accountToken != null) {
                return returnPageBiz.grantSsoToken(redirectUri, state, accountToken);
            }
        }

        /**
         * 保存登录认证请求信息，重定向登录页面
         */
        AuthRequest request = requestBiz.createAndSave(clientId, authorizationGrant, registeredUri, scopes, state);
        return returnPageBiz.loginPage(request, client, model);
    }

    @PostMapping("/approval")
    public String approval(@RequestParam(name = "authorize_id") String authorizeId,
                           @RequestParam(name = "scopes", required = false) String[] scopes,
                           @RequestParam(name = "authorized", defaultValue = "true") boolean authorized,
                           HttpServletResponse response,
                           Model model) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        Client client = clientBiz.findOne(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的客户端");
        }

        if (authorized) {
            approvedScopeBiz.save(client, request.getAccountId(), scopes);
            return returnPageBiz.grantToken(request, response, model);
        } else {
            return returnPageBiz.rejectToken(request);
        }
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
    public ResponseEntity<Object> token(@RequestParam(name = "client_id") String clientId,
                                        @RequestParam(name = "grant_type") String grantType,

                                        @RequestParam(name = "code", required = false) String code,
                                        @RequestParam(name = "client_secret", required = false) String clientSecret,
                                        @RequestParam(name = "redirect_uri", required = false) String redirectUri,

                                        @RequestParam(name = "username", required = false) String username,
                                        @RequestParam(name = "password", required = false) String password,

                                        @RequestParam(name = "refresh_token", required = false) String refreshToken,
                                        HttpServletResponse response) {
        Client client = clientBiz.findOne(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error("无效的客户端"));
        }

        if (!client.getAuthorizationGrants().contains(grantType)) {
            return ResponseEntity.badRequest().body(Constants.error("未授权的grantType"));
        }

        switch (grantType.toLowerCase()) {
            case OAuthConstants.AuthorizationGrant.PASSWORD: {
                /**
                 * 密码模式
                 */
                if (StrUtil.hasBlank(username, password)) {
                    return ResponseEntity.badRequest().body(Constants.error("账号和密码不能为空"));
                }

                try {
                    String accountId = accountBiz.login(username, password);
                    if (accountId != null) {
                        Set<String> scopes = client.getAutoApprovedScopes();
                        AccountToken accountToken = tokenBiz.create(clientId, scopes, grantType, accountId);

                        response.addCookie(Constants.setSsoCookie(accountToken.getAccessToken()));

                        return ResponseEntity.ok(
                                AccessToken.create(
                                        accountToken.getAccessToken(),
                                        accountToken.getRefreshToken(),
                                        client.getAccessTokenValiditySeconds()
                                )
                        );
                    } else {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.error("账号或密码错误"));
                    }
                } catch (AccountLockedException e) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("账号被锁定，请申诉解锁"));
                } catch (PasswordExpiredException e) {
                    return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(Constants.error("密码过期，必须修改密码"));
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Constants.error("账号或密码错误"));
                }
            }
            case OAuthConstants.AuthorizationGrant.AUTHORIZATION_CODE: {
                /**
                 * 授权码模式
                 */
                if (StrUtil.isBlank(code)) {
                    return ResponseEntity.badRequest().body(Constants.error("code不能为空"));
                }
                if (!client.getSecret().equals(clientSecret)) {
                    return ResponseEntity.badRequest().body(Constants.error("client_secret错误"));
                }
                AuthCode authCode = codeBiz.consumeCode(code);
                if (authCode == null) {
                    return ResponseEntity.badRequest().body(Constants.error("无效code"));
                }

                if (!authCode.getRedirectUri().equalsIgnoreCase(redirectUri)) {
                    return ResponseEntity.badRequest().body(Constants.error("redirect_uri不匹配"));
                }

                AccountToken accountToken = tokenBiz.createByCode(authCode);

                response.addCookie(Constants.setSsoCookie(accountToken.getAccessToken()));

                return ResponseEntity.ok(
                        AccessToken.create(
                                accountToken.getAccessToken(),
                                accountToken.getRefreshToken(),
                                client.getAccessTokenValiditySeconds()
                        )
                );
            }
            case OAuthConstants.AuthorizationGrant.REFRESH_TOKEN: {
                /**
                 * 刷新access_token
                 */
                if (StrUtil.isBlank(refreshToken)) {
                    return ResponseEntity.badRequest().body(Constants.error("refresh_token不能为空"));
                }

                AccountToken accountToken = tokenBiz.refreshAccessToken(refreshToken);
                if (accountToken != null) {
                    response.addCookie(Constants.setSsoCookie(accountToken.getAccessToken()));

                    return ResponseEntity.ok(
                            AccessToken.create(
                                    accountToken.getAccessToken(),
                                    accountToken.getRefreshToken(),
                                    client.getAccessTokenValiditySeconds()
                            )
                    );
                } else {
                    return ResponseEntity.badRequest().body(Constants.error("无效的refresh_token"));
                }
            }
            case OAuthConstants.AuthorizationGrant.CLIENT_CREDENTIALS: {
                /**
                 * 凭证式
                 */
                if (!client.getSecret().equals(clientSecret)) {
                    return ResponseEntity.badRequest().body(Constants.error("client_secret错误"));
                }

                /**
                 * 凭证模式的取Client里配置的accountId
                 */
                String accountId = client.getAccountId();
                if (StrUtil.isBlank(accountId)) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Constants.error("服务端配置错误"));
                }

                AccountToken accountToken = tokenBiz.create(clientId, client.getAutoApprovedScopes(), grantType, accountId);

                response.addCookie(Constants.setSsoCookie(accountToken.getAccessToken()));

                return ResponseEntity.ok(
                        AccessToken.create(
                                accountToken.getAccessToken(),
                                accountToken.getRefreshToken(),
                                client.getAccessTokenValiditySeconds()
                        )
                );
            }
            default: {
                return ResponseEntity.badRequest().body(Constants.error("无效的grantType"));
            }
        }
    }
}
