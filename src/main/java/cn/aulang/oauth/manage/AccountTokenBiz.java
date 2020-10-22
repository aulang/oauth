package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.AccountTokenRepository;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.Client;
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
        return dao.findByAccessToken(accessToken);
    }

    public AccountToken findByAccountIdAndClientIdAndRedirectUri(String accountId, String clientId, String redirectUri) {
        return dao.findByAccountIdAndClientIdAndRedirectUri(accountId, clientId, redirectUri);
    }

    public AccountToken save(AccountToken token) {
        AccountToken accountToken = findByAccountIdAndClientIdAndRedirectUri(
                token.getAccountId(),
                token.getClientId(),
                token.getRedirectUri()
        );

        if (accountToken != null) {
            token.setId(accountToken.getId());
        }

        return dao.save(token);
    }

    public AccountToken refreshAccessToken(String refreshToken) {
        try {
            return refreshAccessToken(refreshToken, IdUtil.fastSimpleUUID());
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return null;
        }
    }

    public AccountToken refreshAccessToken(String refreshToken, String newAccessToken) {
        AccountToken accountToken = dao.findByRefreshToken(refreshToken);

        if (accountToken == null) {
            return null;
        }

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime refreshTokenExpiration = accountToken.getRefreshTokenExpiresAt();
        if (refreshTokenExpiration != null && refreshTokenExpiration.isBefore(now)) {
            throw new RuntimeException("令牌已过期");
        }

        accountToken.setAccessToken(newAccessToken);

        Client client = clientBiz.findOne(accountToken.getClientId());
        accountToken.setAccessTokenExpiresAt(now.plusSeconds(client.getAccessTokenValiditySeconds()));
        accountToken.setRefreshTokenExpiresAt(now.plusSeconds(client.getRefreshTokenValiditySeconds()));

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
            throw new RuntimeException("客户端不存在");
        }

        LocalDateTime now = LocalDateTime.now();

        accountToken.setAccessTokenExpiresAt(now.plusSeconds(client.getAccessTokenValiditySeconds()));

        accountToken.setRefreshTokenExpiresAt(now.plusSeconds(client.getRefreshTokenValiditySeconds()));

        return save(accountToken);
    }

    public AccountToken createByCode(AuthCode code) {
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();
        return create(
                accessToken,
                refreshToken,
                code.getClientId(),
                code.getScopes(),
                code.getRedirectUri(),
                code.getAccountId()
        );
    }
}
