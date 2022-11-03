package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.repository.AuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 17:02
 */
@Service
public class AuthCodeBiz {

    private final AuthCodeRepository dao;

    @Autowired
    public AuthCodeBiz(AuthCodeRepository dao) {
        this.dao = dao;
    }

    public AuthCode save(AuthCode entity) {
        return dao.save(entity);
    }

    public void delete(String id) {
        dao.deleteById(id);
    }

    public AuthCode findOne(String id) {
        Optional<AuthCode> optional = dao.findById(id);
        return optional.orElse(null);
    }

    public AuthCode consumeCode(String code) {
        AuthCode authCode = findOne(code);
        if (authCode == null) {
            return null;
        }
        delete(authCode.getId());
        return authCode;
    }

    public AuthCode create(String clientId, Set<String> scopes, String redirectUri, String codeChallenge, String accountId) {
        AuthCode code = new AuthCode();
        code.setCodeChallenge(codeChallenge);
        code.setRedirectUri(redirectUri);
        code.setAccountId(accountId);
        code.setClientId(clientId);
        code.setScopes(scopes);
        return save(code);
    }
}
