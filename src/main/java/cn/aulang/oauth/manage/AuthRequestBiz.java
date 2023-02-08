package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.repository.AuthRequestReRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wulang
 */
@Service
public class AuthRequestBiz {

    private final AuthRequestReRepository dao;

    @Autowired
    public AuthRequestBiz(AuthRequestReRepository dao) {
        this.dao = dao;
    }

    @CachePut(cacheNames = "AuthRequest", key = "#result.id")
    public AuthRequest createAndSave(String clientId,
                                     String authGrant,
                                     String redirectUri,
                                     String codeChallenge,
                                     String state) {

        AuthRequest request = new AuthRequest();

        request.setAccountId(null);
        request.setAuthenticated(false);
        request.setMustChpwd(false);

        request.setClientId(clientId);
        request.setAuthGrant(authGrant);
        request.setRedirectUri(redirectUri);
        request.setCodeChallenge(codeChallenge);
        request.setState(state);

        request.setCreateDate(new Date());

        dao.save(request);
        return request;
    }

    @CachePut(cacheNames = "AuthRequest", key = "#entity.id")
    public AuthRequest save(AuthRequest entity) {
        entity.setUpdateDate(new Date());
        dao.save(entity);
        return entity;
    }

    @Cacheable(cacheNames = "AuthRequest", key = "#id")
    public AuthRequest get(String id) {
        return dao.findById(id).orElse(null);
    }

    @CacheEvict(cacheNames = "AuthRequest", key = "#id")
    public void delete(String id) {
        dao.deleteById(id);
    }
}
