package cn.aulang.oauth.manage;

import cn.aulang.common.core.utils.Identities;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.model.JwtUser;
import cn.aulang.oauth.repository.AuthTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wulang
 */
@Slf4j
@Service
public class AuthTokenBiz {

    private final JwtHelper jwtHelper;
    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final AuthTokenRepository dao;
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public AuthTokenBiz(JwtHelper jwtHelper, ClientBiz clientBiz, AccountBiz accountBiz,
                        AuthTokenRepository dao, ThirdAccountBiz thirdAccountBiz) {
        this.dao = dao;
        this.jwtHelper = jwtHelper;
        this.clientBiz = clientBiz;
        this.accountBiz = accountBiz;
        this.thirdAccountBiz = thirdAccountBiz;
    }

    public AuthToken get(String id) {
        return dao.get(id);
    }

    public AuthToken findByAccountIdAndClientIdAndRedirectUri(String accountId, String clientId, String redirectUri) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        params.put("redirectUri", redirectUri);
        params.put("accountId", accountId);
        return dao.getOneByProperties(AuthToken.class, params);
    }

    public AuthToken save(AuthToken entity) {
        AuthToken authToken = findByAccountIdAndClientIdAndRedirectUri(
                entity.getAccountId(),
                entity.getClientId(),
                entity.getRedirectUri()
        );

        if (authToken != null) {
            entity.setId(authToken.getId());
            entity.setCreateDate(authToken.getCreateDate());
        }

        if (entity.isNew()) {
            entity.setUpdateDate(null);
            entity.setCreateDate(new Date());
        } else {
            entity.setUpdateDate(new Date());
        }

        dao.save(entity);
        return entity;
    }

    public AuthToken refreshAccessToken(String refreshToken) {
        try {
            AuthToken authToken = dao.getOneByProperty(AuthToken.class, "refreshToken", refreshToken);
            if (authToken == null) {
                return null;
            }

            return refreshAccessToken(authToken);
        } catch (Exception e) {
            log.error("刷新令牌失败", e);
            return null;
        }
    }

    public AuthToken refreshAccessToken(AuthToken authToken) throws Exception {
        Date now = new Date();

        Date refreshTokenExpiration = authToken.getRefreshTokenExpiresAt();
        if (refreshTokenExpiration != null && refreshTokenExpiration.before(now)) {
            throw new RuntimeException("令牌已过期");
        }

        String clientId = authToken.getClientId();
        JwtUser jwtUser = accountBiz.getProfile(authToken.getAccountId(), clientId, authToken.getRefreshToken());

        Client client = clientBiz.get(clientId);
        Date accessTokenExpiresAt = DateUtils.addSeconds(now, client.getAccessTokenExpiresIn());
        authToken.setAccessTokenExpiresAt(accessTokenExpiresAt);

        String newAccessToken = jwtHelper.encode(jwtUser, accessTokenExpiresAt);
        authToken.setAccessToken(newAccessToken);

        dao.save(authToken);
        return authToken;
    }

    public AuthToken create(
            String clientId,
            String redirectUri,
            String accountId) {
        Client client = clientBiz.get(clientId);
        if (client == null) {
            throw new RuntimeException("客户端不存在");
        }

        Date now = new Date();
        Date accessTokenExpiresAt = DateUtils.addSeconds(now, client.getAccessTokenExpiresIn());

        String accessToken;
        String refreshToken = Identities.uuid2();

        try {
            JwtUser jwtUser;
            if (Constants.NA.equals(accountId)) {
                jwtUser = new JwtUser();
                jwtUser.setClientId(clientId);
                jwtUser.setTokenId(refreshToken);
            } else {
                jwtUser = accountBiz.getProfile(accountId, clientId, refreshToken);
            }
            accessToken = jwtHelper.encode(jwtUser, accessTokenExpiresAt);
        } catch (Exception e) {
            throw new RuntimeException("生成access_token失败", e);
        }

        AuthToken authToken = new AuthToken();
        authToken.setClientId(clientId);
        authToken.setRedirectUri(redirectUri);
        authToken.setAccountId(accountId);
        authToken.setAccessToken(accessToken);
        authToken.setRefreshToken(refreshToken);

        authToken.setAccessTokenExpiresAt(accessTokenExpiresAt);
        authToken.setRefreshTokenExpiresAt(DateUtils.addSeconds(now, client.getRefreshTokenExpiresIn()));

        return save(authToken);
    }

    public AuthToken createByCode(AuthCode code) {
        return create(
                code.getClientId(),
                code.getRedirectUri(),
                code.getAccountId()
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public AuthToken bindToken(ThirdServer thirdServer, String clientId, String thirdId, String openId, String unionId, String accountId) {
        thirdAccountBiz.bind(thirdServer.getId(), thirdServer.getType(), thirdId, thirdId, openId, unionId, accountId);
        return create(clientId, thirdServer.getName(), accountId);
    }
}
