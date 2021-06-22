package cn.aulang.oauth.controller;

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
import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> token(
            @RequestParam(name = "client_id") String clientId,
            @RequestParam(name = "grant_type") String grantType,
            // authorization_code
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            @RequestParam(name = "code_verifier", required = false) String codeVerifier,
            // refresh_token
            @RequestParam(name = "refresh_token", required = false) String refreshToken,
            // client_credentials
            @RequestParam(name = "client_secret", required = false) String clientSecret) {
        Client client = clientBiz.findOne(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body("client_id错误");
        }

        if (client.getAuthorizationGrants() != null && !client.getAuthorizationGrants().contains(grantType)) {
            return ResponseEntity.badRequest().body("未授权的grant_type");
        }

        if (AuthorizationGrant.CODE.getGrantType().equalsIgnoreCase(grantType)) {
            return code(clientId, code, redirectUri, codeVerifier);
        }

        if (AuthorizationGrant.REFRESH_TOKEN.getGrantType().equalsIgnoreCase(grantType)) {
            return refresh(refreshToken);
        }

        if (AuthorizationGrant.CLIENT_CREDENTIALS.getGrantType().equalsIgnoreCase(grantType)) {
            return clientCredentials(client, clientSecret);
        }

        return ResponseEntity.badRequest().body("grant_type错误");
    }

    private ResponseEntity<?> code(String clientId,
                                   String code,
                                   String redirectUri,
                                   String codeVerifier) {
        if (StrUtil.hasBlank(code, redirectUri, codeVerifier)) {
            return ResponseEntity.badRequest().body("参数缺失");
        }

        AuthCode authCode = authCodeBiz.findOne(code);
        if (authCode == null) {
            return ResponseEntity.badRequest().body("code已过期");
        }

        if (authCode.isSso()) {
            // 单点登录
            AccountToken accountToken = accountTokenBiz.findByAuthId(authCode.getAuthId());
            if (accountToken != null) {
                // Code被消费
                authCodeBiz.consumeCode(code);
                return ResponseEntity.ok(AccessToken.build(accountToken));
            }
        }

        AuthRequest authRequest = authRequestBiz.findOne(authCode.getAuthId());
        if (authRequest == null) {
            return ResponseEntity.badRequest().body("认证请求已过期");
        }

        if (!clientId.equalsIgnoreCase(authRequest.getClientId())) {
            return ResponseEntity.badRequest().body("client_id不匹配");
        }

        if (!redirectUri.equalsIgnoreCase(authRequest.getRedirectUri())) {
            return ResponseEntity.badRequest().body("redirect_uri不匹配");
        }

        String codeChallenge = Base64.encodeUrlSafe(DigestUtil.sha256(codeVerifier));
        if (!codeChallenge.equals(authRequest.getCodeChallenge())) {
            return ResponseEntity.badRequest().body("code_verifier错误");
        }

        try {
            AccountToken accountToken = accountTokenBiz.create(
                    authRequest.getId(),
                    authRequest.getClientId(),
                    authRequest.getScopes(),
                    authRequest.getRedirectUri(),
                    authRequest.getAccountId()
            );

            // Code被消费
            authCodeBiz.consumeCode(code);

            return ResponseEntity.ok(AccessToken.build(accountToken));
        } catch (Exception e) {
            log.error("创建Token失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private ResponseEntity<?> refresh(String refreshToken) {
        try {
            AccountToken accountToken = accountTokenBiz.refreshAccessToken(refreshToken);
            return ResponseEntity.ok(AccessToken.build(accountToken));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private ResponseEntity<?> clientCredentials(Client client, String clientSecret) {
        if (StrUtil.isBlank(clientSecret) || !clientSecret.equals(client.getSecret())) {
            return ResponseEntity.badRequest().body("client_secret错误");
        }

        if (StrUtil.isBlank(client.getAccountId())) {
            return ResponseEntity.badRequest().body("client_id未配置绑定账号");
        }

        try {
            AccountToken accountToken = accountTokenBiz.create(
                    client.getId(),
                    client.getId(),
                    client.getAutoApprovedScopes(),
                    AuthorizationGrant.CLIENT_CREDENTIALS.getGrantType(),
                    client.getAccountId()
            );

            return ResponseEntity.ok(AccessToken.build(accountToken));
        } catch (Exception e) {
            log.error("创建Token失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
