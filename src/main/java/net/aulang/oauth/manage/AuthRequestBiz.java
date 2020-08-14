package net.aulang.oauth.manage;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.aulang.oauth.common.Constants;
import net.aulang.oauth.common.OAuthConstants;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.repository.AuthRequestReRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
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
    private AuthCodeBiz codeBiz;
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountTokenBiz tokenBiz;
    @Autowired
    private AuthRequestReRepository dao;

    public AuthRequest create(String accountId,
                              String clientId,
                              String authorizationGrant,
                              String redirectUri,
                              Set<String> scopes,
                              String state) {

        AuthRequest request = new AuthRequest();

        request.setAccountId(accountId);
        request.setAuthenticated(true);

        request.setClientId(clientId);
        request.setAuthorizationGrant(authorizationGrant);
        request.setRedirectUri(redirectUri);
        request.setScopes(scopes);
        request.setState(state);

        return request;
    }

    public AuthRequest createAndSave(String clientId,
                                     String authorizationGrant,
                                     String redirectUri,
                                     Set<String> scopes,
                                     String state) {

        AuthRequest request = new AuthRequest();

        request.setClientId(clientId);
        request.setAuthorizationGrant(authorizationGrant);
        request.setRedirectUri(redirectUri);
        request.setScopes(scopes);
        request.setState(state);

        return dao.save(request);
    }

    public AuthRequest save(AuthRequest entity) {
        return dao.save(entity);
    }

    public AuthRequest findOne(String id) {
        Optional<AuthRequest> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public String redirect(AuthRequest request, HttpServletResponse response, Model model) {
        StringBuilder redirectUri = new StringBuilder(Constants.REDIRECT);
        redirectUri.append(request.getRedirectUri());

        /**
         * 有?不用再添加?直接添加&
         */
        if (redirectUri.lastIndexOf(Constants.QUESTION) == -1) {
            redirectUri.append(Constants.QUESTION);
        } else {
            redirectUri.append(Constants.AND);
        }

        /**
         * 如果有state要添加state
         * state=STATE&
         */
        String state = request.getState();
        if (StrUtil.isNotBlank(state)) {
            redirectUri
                    .append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(state)
                    .append(Constants.AND);
        }

        switch (request.getAuthorizationGrant()) {
            case OAuthConstants.AuthorizationGrant.IMPLICIT: {
                /**
                 * 简化模式: access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN
                 */
                Client client = clientBiz.findOne(request.getClientId());
                String accessToken = tokenBiz.create(
                        request.getClientId(),
                        request.getScopes(),
                        request.getRedirectUri(),
                        request.getAccountId()
                ).getAccessToken();
                long expires_in = client.getAccessTokenValiditySeconds();
                redirectUri
                        .append(OAuthConstants.ACCESS_TOKEN).append(Constants.EQUAL).append(accessToken)
                        .append(Constants.AND)
                        .append(OAuthConstants.EXPIRES_IN).append(Constants.EQUAL).append(expires_in);

                if (response != null && !response.isCommitted()) {
                    response.addCookie(Constants.setSsoCookie(accessToken));
                }

                return redirectUri.toString();
            }
            case OAuthConstants.AuthorizationGrant.AUTHORIZATION_CODE: {
                /**
                 * 授权码模式：code=CODE
                 */
                String code = codeBiz.create(
                        request.getClientId(),
                        request.getScopes(),
                        request.getRedirectUri(),
                        request.getAccountId()
                ).getId();

                redirectUri.append(OAuthConstants.CODE).append(Constants.EQUAL).append(code);

                return redirectUri.toString();
            }
            default: {
                log.error("错误的授权方式，登录认证ID：{}，授权方式：{}", request.getId(), request.getAuthorizationGrant());
                redirectUri = new StringBuilder(Constants.errorPage(model, "错误的授权方式"));
                return redirectUri.toString();
            }
        }
    }
}
