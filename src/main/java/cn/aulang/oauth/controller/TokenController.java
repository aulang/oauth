package cn.aulang.oauth.controller;

import cn.aulang.framework.exception.CommonError;
import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.bo.AccessToken;
import cn.aulang.oauth.model.enums.AuthorizationGrant;
import cn.aulang.oauth.model.request.TokenRequest;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 令牌控制器
 *
 * @author Aulang
 * @date 2021-06-19 22:54
 */
@Slf4j
@RestController
@RequestMapping("/api/token")
public class TokenController {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AuthCodeBiz authCodeBiz;
    @Autowired
    private AuthRequestBiz authRequestBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    @PostMapping("")
    public Response<?> token(@Valid @RequestBody TokenRequest request) {
        Client client = clientBiz.getClient(request.getClientId());

        String grantType = request.getGrantType();
        if (client.getAuthorizationGrants() != null
                && !client.getAuthorizationGrants().contains(grantType)) {
            throw OAuthError.GRANT_TYPE_UNAUTHORIZED.exception();
        }

        if (AuthorizationGrant.CODE.getGrantType().equalsIgnoreCase(grantType)) {
            return code(request.getClientId(), request.getCode(), request.getRedirectUri(), request.getCodeVerifier());
        }

        if (AuthorizationGrant.REFRESH_TOKEN.getGrantType().equalsIgnoreCase(grantType)) {
            return refresh(request.getRefreshToken());
        }

        if (AuthorizationGrant.CLIENT_CREDENTIALS.getGrantType().equalsIgnoreCase(grantType)) {
            return clientCredentials(client, request.getClientSecret());
        }

        if (AuthorizationGrant.CAPTCHA.getGrantType().equalsIgnoreCase(grantType)) {
            return captcha(request.getAuthId(), request.getMobile(), request.getCaptcha());
        }

        throw OAuthError.GRANT_TYPE_UNAUTHORIZED.exception();
    }

    private Response<AccessToken> code(String clientId,
                                       String code,
                                       String redirectUri,
                                       String codeVerifier) {
        if (StrUtil.hasBlank(code, redirectUri, codeVerifier)) {
            throw CommonError.BAD_REQUEST.exception();
        }

        AuthCode authCode = authCodeBiz.findOne(code);
        if (authCode == null) {
            throw OAuthError.CODE_EXPIRED.exception();
        }

        AuthRequest authRequest = authRequestBiz.getAuthRequest(authCode.getId());

        if (!clientId.equalsIgnoreCase(authRequest.getClientId())) {
            throw OAuthError.CLIENT_ID_MISMATCH.exception();
        }

        if (!redirectUri.equalsIgnoreCase(authRequest.getRedirectUri())) {
            throw OAuthError.REDIRECT_URI_ERROR.exception();
        }

        String codeChallenge = Base64.encodeUrlSafe(DigestUtil.sha256(codeVerifier));
        if (!codeChallenge.equals(authRequest.getCodeChallenge())) {
            throw OAuthError.CODE_VERIFIER_ERROR.exception();
        }

        // Code被消费
        authCodeBiz.consumeCode(code);

        AccountToken accountToken;
        if (authCode.isSso()) {
            // 单点登录
            accountToken = accountTokenBiz.findByAuthId(authCode.getAuthId());
        } else {
            // 非单点登录
            accountToken = accountTokenBiz.create(
                    authRequest.getId(),
                    authRequest.getClientId(),
                    authRequest.getScopes(),
                    authRequest.getRedirectUri(),
                    authRequest.getAccountId()
            );
        }

        return ResponseFactory.success(AccessToken.build(accountToken));
    }

    private Response<AccessToken> captcha(String authId,
                                          String mobile,
                                          String captcha) {
        if (StrUtil.hasBlank(authId, mobile, captcha)) {
            throw CommonError.BAD_REQUEST.exception();
        }

        AuthRequest authRequest = authRequestBiz.getAuthRequest(authId);

        if (!StrUtil.equals(mobile, authRequest.getMobile())
                || !StrUtil.equals(captcha, authRequest.getCaptcha())) {
            throw OAuthError.CAPTCHA_ERROR.exception();
        }

        AccountToken accountToken = accountTokenBiz.create(
                authRequest.getId(),
                authRequest.getClientId(),
                authRequest.getScopes(),
                authRequest.getRedirectUri(),
                authRequest.getAccountId()
        );

        return ResponseFactory.success(AccessToken.build(accountToken));
    }

    private Response<AccessToken> refresh(String refreshToken) {
        AccountToken accountToken = accountTokenBiz.refreshAccessToken(refreshToken);
        return ResponseFactory.success(AccessToken.build(accountToken));
    }

    private Response<AccessToken> clientCredentials(Client client, String clientSecret) {
        if (StrUtil.isBlank(clientSecret) || !clientSecret.equals(client.getSecret())) {
            throw OAuthError.CLIENT_CREDENTIALS_ERROR.exception("client_secret错误");
        }

        if (StrUtil.isBlank(client.getAccountId())) {
            throw OAuthError.CLIENT_CREDENTIALS_ERROR.exception("client_id未配置绑定账号");
        }

        AccountToken accountToken = accountTokenBiz.create(
                client.getId(),
                client.getId(),
                client.getAutoApprovedScopes(),
                AuthorizationGrant.CLIENT_CREDENTIALS.getGrantType(),
                client.getAccountId()
        );

        return ResponseFactory.success(AccessToken.build(accountToken));
    }
}
