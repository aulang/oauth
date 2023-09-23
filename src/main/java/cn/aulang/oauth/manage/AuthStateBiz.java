package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.AuthStateRepository;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AuthState;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wulang
 */
@Service
public class AuthStateBiz {

    private final AuthStateRepository dao;

    @Autowired
    public AuthStateBiz(AuthStateRepository dao) {
        this.dao = dao;
    }

    public AuthState save(AuthState entity) {
        dao.save(entity);
        return entity;
    }

    public AuthState create(String authorizeId, String serverId, String accountId) {
        AuthState state = new AuthState();
        state.setAuthorizeId(authorizeId);
        state.setServerId(serverId);
        state.setAccountId(accountId);
        return save(state);
    }

    public AuthState getByState(String state) {
        AuthState authState = dao.get(state);

        if (authState == null) {
            return null;
        }

        Date tenMinutesLater = DateUtils.addMinutes(authState.getCreateDate(), OAuthConstants.DEFAULT_EXPIRES_MINUTES);
        if (tenMinutesLater.before(new Date())) {
            dao.deleteByPrimaryKey(authState.getId());
            return null;
        }

        return authState;
    }
}
