package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.repository.AuthRequestReRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:26
 */
@Service
public class AuthRequestBiz {

    private final AuthRequestReRepository dao;

    @Autowired
    public AuthRequestBiz(AuthRequestReRepository dao) {
        this.dao = dao;
    }

    public AuthRequest createAndSave(String accountId,
                                     String clientId,
                                     String authorizationGrant,
                                     String redirectUri,
                                     Set<String> scopes,
                                     String codeChallenge,
                                     String state) {

        AuthRequest request = new AuthRequest();

        request.setAccountId(accountId);
        request.setAuthenticated(true);

        request.setClientId(clientId);
        request.setAuthorizationGrant(authorizationGrant);
        request.setRedirectUri(redirectUri);
        request.setScopes(scopes);
        request.setCodeChallenge(codeChallenge);
        request.setState(state);

        return dao.save(request);
    }

    public AuthRequest createAndSave(String clientId,
                                     String authorizationGrant,
                                     String redirectUri,
                                     Set<String> scopes,
                                     String codeChallenge,
                                     String state) {

        AuthRequest request = new AuthRequest();

        request.setAccountId(null);
        request.setAuthenticated(false);

        request.setClientId(clientId);
        request.setAuthorizationGrant(authorizationGrant);
        request.setRedirectUri(redirectUri);
        request.setScopes(scopes);
        request.setCodeChallenge(codeChallenge);
        request.setState(state);

        return dao.save(request);
    }

    public AuthRequest save(AuthRequest entity) {
        return dao.save(entity);
    }

    public AuthRequest findOne(String id) {
        Optional<AuthRequest> optional = dao.findById(id);
        return optional.orElse(null);
    }
}
