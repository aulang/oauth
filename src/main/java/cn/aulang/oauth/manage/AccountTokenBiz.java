package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.model.Profile;
import cn.aulang.oauth.repository.AccountTokenRepository;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wulang
 */
@Slf4j
@Service
public class AccountTokenBiz {

    private final JwtHelper jwtHelper;
    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final AccountTokenRepository dao;

    @Autowired
    public AccountTokenBiz(JwtHelper jwtHelper, ClientBiz clientBiz, AccountBiz accountBiz, AccountTokenRepository dao) {
        this.jwtHelper = jwtHelper;
        this.clientBiz = clientBiz;
        this.accountBiz = accountBiz;
        this.dao = dao;
    }

    public AccountToken get(String id) {
        return dao.findById(id).orElse(null);
    }

    public AccountToken save(AccountToken entity) {
        AccountToken accountToken = dao.findByClientIdAndRedirectUriAndAccountId(
                entity.getClientId(),
                entity.getRedirectUri(),
                entity.getAccountId()
        );

        if (accountToken != null) {
            entity.setId(accountToken.getId());
        }

        if (entity.isNew()) {
            entity.setUpdateDate(null);
            entity.setCreateDate(new Date());
        } else {
            entity.setCreateDate(null);
            entity.setUpdateDate(new Date());
        }

        dao.save(entity);
        return entity;
    }

    public AccountToken refreshAccessToken(String refreshToken) {
        try {
            AccountToken accountToken = dao.findByRefreshToken(refreshToken);
            if (accountToken == null) {
                return null;
            }

            return refreshAccessToken(accountToken);
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return null;
        }
    }

    public AccountToken refreshAccessToken(AccountToken accountToken) throws Exception {
        Date now = new Date();

        Date refreshTokenExpiration = accountToken.getRefreshTokenExpiresAt();
        if (refreshTokenExpiration != null && refreshTokenExpiration.before(now)) {
            throw new RuntimeException("令牌已过期");
        }

        String clientId = accountToken.getClientId();
        Profile profile = accountBiz.getProfile(accountToken.getAccountId(), clientId);

        Client client = clientBiz.get(clientId);
        Date accessTokenExpiresAt = DateUtil.offsetSecond(now, client.getAccessTokenExpiresIn());

        String newAccessToken = jwtHelper.encode(profile, accessTokenExpiresAt);
        accountToken.setAccessToken(newAccessToken);

        accountToken.setAccessTokenExpiresAt(accessTokenExpiresAt);
        accountToken.setRefreshTokenExpiresAt(DateUtil.offsetSecond(now, client.getRefreshTokenExpiresIn()));

        return save(accountToken);
    }

    public AccountToken create(
            String clientId,
            String redirectUri,
            String accountId) {
        Client client = clientBiz.get(clientId);
        if (client == null) {
            throw new RuntimeException("客户端不存在");
        }

        Date now = new Date();
        Date accessTokenExpiresAt = DateUtil.offsetSecond(now, client.getAccessTokenExpiresIn());

        String accessToken;
        try {
            Profile profile = accountBiz.getProfile(accountId, clientId);
            accessToken = jwtHelper.encode(profile, accessTokenExpiresAt);
        } catch (Exception e) {
            throw new RuntimeException("生成access_token失败", e);
        }

        AccountToken accountToken = new AccountToken();
        accountToken.setClientId(clientId);
        accountToken.setRedirectUri(redirectUri);
        accountToken.setAccountId(accountId);
        accountToken.setAccessToken(accessToken);
        accountToken.setRefreshToken(IdUtil.fastSimpleUUID());

        accountToken.setAccessTokenExpiresAt(accessTokenExpiresAt);
        accountToken.setRefreshTokenExpiresAt(DateUtil.offsetSecond(now, client.getRefreshTokenExpiresIn()));

        return save(accountToken);
    }

    public AccountToken createByCode(AuthCode code) {
        return create(
                code.getClientId(),
                code.getRedirectUri(),
                code.getAccountId()
        );
    }
}
