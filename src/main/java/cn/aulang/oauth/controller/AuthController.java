package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.enums.AuthorizationGrant;
import cn.aulang.oauth.model.request.AuthorizeRequest;
import cn.aulang.oauth.model.response.AuthRequestVO;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 认证请求控制器
 *
 * @author Aulang
 * @date 2021-06-17 21:06
 */
@RestController
public class AuthController {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AuthRequestBiz authRequestBiz;

    @PostMapping("/api/authorize")
    public Response<AuthRequestVO> authorize(@Valid @RequestBody AuthorizeRequest request) {
        // 判断客户端是否存在
        Client client = clientBiz.getClient(request.getClientId());

        if (!AuthorizationGrant.CODE.getResponseType().equalsIgnoreCase(request.getResponseType())) {
            throw OAuthError.RESPONSE_TYPE_ERROR.exception();
        }

        if (client.getAuthorizationGrants() != null
                && !client.getAuthorizationGrants().contains(AuthorizationGrant.CODE.getGrantType())) {
            throw OAuthError.GRANT_TYPE_UNAUTHORIZED.exception();
        }

        // 判断redirect uri是否匹配，支持正则，一个客户端可以配置多个redirect uri
        String redirectUri = request.getRedirectUri();

        Set<String> registeredUrls = client.getRegisteredRedirectUris();
        if (registeredUrls == null) {
            throw OAuthError.REDIRECT_URI_ERROR.exception();
        }

        boolean match = registeredUrls.parallelStream().anyMatch(url -> {
            var pattern = Pattern.compile(url, Pattern.CASE_INSENSITIVE);
            return pattern.matcher(redirectUri).matches();
        });
        if (!match) {
            throw OAuthError.REDIRECT_URI_ERROR.exception();
        }

        // 有scope时，scope不能超出客户端的范围
        String scope = request.getScope();
        Set<String> scopes = new HashSet<>();
        if (StrUtil.isNotBlank(scope)) {
            List<String> requestScopes = Arrays.asList(scope.split(Constants.COMMA));
            if (!client.getScopes().keySet().containsAll(requestScopes)) {
                throw OAuthError.SCOPE_ERROR.exception();
            }
            scopes.addAll(requestScopes);
        } else {
            if (client.getAutoApprovedScopes() != null) {
                scopes.addAll(client.getAutoApprovedScopes());
            }
        }

        // 创建和保存认证请求
        AuthRequest authRequest = authRequestBiz.createAndSave(
                request.getClientId(),
                request.getResponseType(),
                redirectUri,
                request.getCodeChallenge(),
                scopes,
                request.getState()
        );

        // 返回认证ID
        return ResponseFactory.success(AuthRequestVO.of(authRequest.getId(), false));
    }
}
