package net.aulang.oauth.manage;

import net.aulang.oauth.entity.AuthCode;
import net.aulang.oauth.repository.AuthCodeRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 17:02
 */
@Service
public class AuthCodeBiz {
    @Resource
    private AuthCodeRepository dao;

    public AuthCode save(AuthCode entity) {
        return dao.save(entity);
    }

    public void delete(String id) {
        dao.deleteById(id);
    }

    public AuthCode findOne(String id) {
        Optional<AuthCode> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public AuthCode consumeCode(String code) {
        AuthCode authCode = findOne(code);
        if (authCode == null) {
            return null;
        }
        delete(authCode.getId());
        return authCode;
    }

    public AuthCode create(String clientId, Set<String> scopes, String redirectUrl, String accountId) {
        AuthCode code = new AuthCode();
        code.setRedirectUrl(redirectUrl);
        code.setAccountId(accountId);
        code.setClientId(clientId);
        code.setScopes(scopes);
        return save(code);
    }
}
