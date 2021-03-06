package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.framework.exception.CommonError;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.repository.AuthRequestReRepository;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:26
 */
@Slf4j
@Service
public class AuthRequestBiz {
    @Autowired
    private AuthRequestReRepository dao;

    public AuthRequest createAndSave(String clientId,
                                     String responseType,
                                     String redirectUri,
                                     String codeChallenge,
                                     Set<String> scopes,
                                     String state) {

        AuthRequest request = new AuthRequest();

        request.setAccountId(null);
        request.setAuthenticated(false);

        request.setClientId(clientId);
        request.setResponseType(responseType);
        request.setRedirectUri(redirectUri);
        request.setCodeChallenge(codeChallenge);
        request.setScopes(scopes);
        request.setState(state);

        return dao.save(request);
    }

    public AuthRequest save(AuthRequest entity) {
        return dao.save(entity);
    }

    public AuthRequest findOne(String id) {
        return dao.findById(id).orElse(null);
    }

    public AuthRequest checkAuthenticated(String id) throws BaseException {
        AuthRequest authRequest = getAuthRequest(id);

        if (!authRequest.isAuthenticated() && StrUtil.isBlank(authRequest.getAccountId())) {
            throw CommonError.BAD_REQUEST.exception();
        }

        return authRequest;
    }

    public AuthRequest getAuthRequest(String id) throws BaseException {
        return dao.findById(id).orElseThrow(OAuthError.AUTH_REQUEST_NOT_FOUND::exception);
    }
}
