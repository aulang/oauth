package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.AccountTokenRepository;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:17
 */
@Slf4j
@Service
public class AccountTokenBiz {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountTokenRepository dao;

    public AccountToken findByAuthorization(String authorization) throws BaseException {
        String accessToken = StrUtil.removePrefix(authorization, Constants.BEARER).trim();
        return findByAccessToken(accessToken);
    }

    public AccountToken findByAuthId(String authId) {
        AccountToken accountToken = dao.findByAuthId(authId);

        if (isExpires(accountToken)) {
            return null;
        }

        return accountToken;
    }

    public AccountToken findByAccessToken(String accessToken) throws BaseException {
        AccountToken accountToken = dao.findByAccessToken(accessToken);
        if (accountToken == null) {
            throw OAuthError.TOKEN_NOT_FOUND.exception();
        }
        if (isExpires(accountToken)) {
            throw OAuthError.TOKEN_EXPIRED.exception();
        }
        return accountToken;
    }

    private boolean isExpires(AccountToken accountToken) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime accessTokenExpiration = accountToken.getAccessTokenExpiresAt();
        return accessTokenExpiration != null && accessTokenExpiration.isBefore(now);
    }

    public AccountToken save(AccountToken token) {
        return dao.save(token);
    }

    public AccountToken refreshAccessToken(String refreshToken) throws BaseException {
        return refreshAccessToken(refreshToken, IdUtil.fastSimpleUUID());
    }

    public AccountToken refreshAccessToken(String refreshToken, String newAccessToken) throws BaseException {
        AccountToken accountToken = dao.findByRefreshToken(refreshToken);

        if (accountToken == null) {
            throw OAuthError.TOKEN_EXPIRED.exception();
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime refreshTokenExpiration = accountToken.getRefreshTokenExpiresAt();
        if (refreshTokenExpiration != null && refreshTokenExpiration.isBefore(now)) {
            throw OAuthError.TOKEN_EXPIRED.exception();
        }

        accountToken.setAccessToken(newAccessToken);

        accountToken.setAccessTokenExpiresAt(now.plusSeconds(accountToken.getExpiresIn()));
        accountToken.setRefreshTokenExpiresAt(now.plusSeconds(accountToken.getExpiresIn()));

        return save(accountToken);
    }

    public AccountToken create(
            String authId,
            String clientId,
            Set<String> scopes,
            String redirectUri,
            String accountId) throws BaseException {
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();
        return create(accessToken, refreshToken, authId, clientId, scopes, redirectUri, accountId);
    }

    public AccountToken create(
            String accessToken,
            String refreshToken,
            String authId,
            String clientId,
            Set<String> scopes,
            String redirectUri,
            String accountId) throws BaseException {
        AccountToken accountToken = new AccountToken();

        accountToken.setAuthId(authId);
        accountToken.setScopes(scopes);
        accountToken.setClientId(clientId);
        accountToken.setRedirectUri(redirectUri);
        accountToken.setAccountId(accountId);
        accountToken.setAccessToken(accessToken);
        accountToken.setRefreshToken(refreshToken);

        Client client = clientBiz.getClient(clientId);

        LocalDateTime now = LocalDateTime.now();

        accountToken.setAccessTokenExpiresAt(now.plusSeconds(client.getAccessTokenValiditySeconds()));

        accountToken.setExpiresIn(client.getAccessTokenValiditySeconds());

        accountToken.setRefreshTokenExpiresAt(now.plusSeconds(client.getRefreshTokenValiditySeconds()));

        return save(accountToken);
    }
}
