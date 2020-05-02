package net.aulang.oauth.manage;

import net.aulang.oauth.entity.AuthState;
import net.aulang.oauth.repository.AuthStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    public AuthState create(String authorizeId, String serverId, String accountId) {
        AuthState state = new AuthState();
        state.setAuthorizeId(authorizeId);
        state.setThirdServerId(serverId);
        state.setAccountId(accountId);
        return save(state);
    }

    public AuthState findByState(String state) {
        Optional<AuthState> optional = dao.findById(state);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }
}
