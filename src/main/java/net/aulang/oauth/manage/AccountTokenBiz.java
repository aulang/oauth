package net.aulang.oauth.manage;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import net.aulang.oauth.entity.AccountToken;
import net.aulang.oauth.entity.AuthCode;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.repository.AccountTokenRepository;
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

    public AccountToken findByAccountIdAndClientIdAndRedirectUrl(String accountId, String clientId, String redirectUrl) {
        return dao.findByAccountIdAndClientIdAndRedirectUrl(accountId, clientId, redirectUrl);
    }

    public AccountToken save(AccountToken token) {
        AccountToken accountToken = findByAccountIdAndClientIdAndRedirectUrl(
                token.getAccountId(),
                token.getClientId(),
                token.getRedirectUrl()
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
            String redirectUrl,
            String accountId) {
        String accessToken = IdUtil.fastSimpleUUID();
        String refreshToken = IdUtil.fastSimpleUUID();
        return create(accessToken, refreshToken, clientId, scopes, redirectUrl, accountId);
    }

    public AccountToken create(
            String accessToken,
            String refreshToken,
            String clientId,
            Set<String> scopes,
            String redirectUrl,
            String accountId) {
        AccountToken accountToken = new AccountToken();

        accountToken.setScopes(scopes);
        accountToken.setClientId(clientId);
        accountToken.setRedirectUrl(redirectUrl);
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
                code.getRedirectUrl(),
                code.getAccountId()
        );
    }
}
