package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.repository.AuthStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 18:03
 */
@Service
public class AuthStateBiz {
    @Autowired
    private AuthStateRepository dao;

    public AuthState save(AuthState entity) {
        return dao.save(entity);
    }

    public AuthState create(String authId, String serverId, String accountId) {
        AuthState state = new AuthState();
        state.setAuthId(authId);
        state.setThirdServerId(serverId);
        state.setAccountId(accountId);
        return save(state);
    }

    public AuthState findByState(String state) {
        return dao.findById(state).orElse(null);
    }

    public AuthState getAuthState(String id) throws BaseException {
        return dao.findById(id).orElseThrow(OAuthError.AUTH_REQUEST_NOT_FOUND::exception);
    }
}
