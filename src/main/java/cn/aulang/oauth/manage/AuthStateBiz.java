package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.repository.AuthStateRepository;
import cn.hutool.core.date.DateUtil;
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
        state.setThirdServerId(serverId);
        state.setAccountId(accountId);
        return save(state);
    }

    public AuthState getByState(String state) {
        AuthState authState = dao.findById(state).orElse(null);

        if (authState == null) {
            return null;
        }

        Date tenMinutesLater = DateUtil.offsetMinute(authState.getCreateDate(), OAuthConstants.DEFAULT_EXPIRES_MINUTES);
        if (tenMinutesLater.before(new Date())) {
            dao.deleteById(authState.getId());
            return null;
        }

        return authState;
    }
}
