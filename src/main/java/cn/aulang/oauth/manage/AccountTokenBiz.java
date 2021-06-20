package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.AccountTokenRepository;
import cn.hutool.core.util.IdUtil;
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

    public AccountToken findByAccessToken(String accessToken) {
        AccountToken accountToken = dao.findByAccessToken(accessToken);
        if (accountToken == null) {
            throw OAuthError.TOKEN_NOT_FOUND.exception();
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime accessTokenExpiration = accountToken.getAccessTokenExpiresAt();
        if (accessTokenExpiration != null && accessTokenExpiration.isBefore(now)) {
            throw OAuthError.TOKEN_EXPIRED.exception();
        }
        return accountToken;
    }

    public AccountToken findByAccountIdAndClientIdAndRedirectUri(String accountId, String clientId, String redirectUri) {
        return dao.findByAccountIdAndClientIdAndRedirectUri(accountId, clientId, redirectUri);
    }

    public AccountToken save(AccountToken token) {
        // 同一个client，同一个账号，同一个端只能有一个token
        AccountToken accountToken = findByAccountIdAndClientIdAndRedirectUri(
                token.getAccountId(),
                token.getClientId(),
                token.getRedirectUri()
        );

        // 申请新的，之前的就失效
        if (accountToken != null) {
            token.setId(accountToken.getId());
        }

        return dao.save(token);
    }

    public AccountToken refreshAccessToken(String refreshToken) {
        return refreshAccessToken(refreshToken, IdUtil.fastSimpleUUID());
    }

    public AccountToken refreshAccessToken(String refreshToken, String newAccessToken) {
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
            String clientId,
            Set<String> scopes,
            String redirectUri,
            String accountId) {
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();
        return create(accessToken, refreshToken, clientId, scopes, redirectUri, accountId);
    }

    public AccountToken create(
            String accessToken,
            String refreshToken,
            String clientId,
            Set<String> scopes,
            String redirectUri,
            String accountId) {
        AccountToken accountToken = new AccountToken();

        accountToken.setScopes(scopes);
        accountToken.setClientId(clientId);
        accountToken.setRedirectUri(redirectUri);
        accountToken.setAccountId(accountId);
        accountToken.setAccessToken(accessToken);
        accountToken.setRefreshToken(refreshToken);

        Client client = clientBiz.findOne(clientId);
        if (client == null) {
            throw OAuthError.CLIENT_NOT_FOUND.exception();
        }

        LocalDateTime now = LocalDateTime.now();

        accountToken.setAccessTokenExpiresAt(now.plusSeconds(client.getAccessTokenValiditySeconds()));

        accountToken.setExpiresIn(client.getAccessTokenValiditySeconds());

        accountToken.setRefreshTokenExpiresAt(now.plusSeconds(client.getRefreshTokenValiditySeconds()));

        return save(accountToken);
    }
}
