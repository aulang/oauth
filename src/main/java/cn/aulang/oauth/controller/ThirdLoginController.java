package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.auth.AuthServiceProvider;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthStateBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.model.request.ThirdLoginRequest;
import cn.aulang.oauth.model.response.AuthCodeVO;
import cn.aulang.oauth.model.response.ThirdAuthVO;
import cn.aulang.oauth.server.core.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 第三方登录控制器
 *
 * @author Aulang
 * @date 2021-06-20 17:17
 */
@RestController
@RequestMapping("/api/third")
public class ThirdLoginController {
    @Autowired
    private AuthCodeBiz authCodeBiz;
    @Autowired
    private AuthStateBiz authStateBiz;
    @Autowired
    private AuthServiceProvider provider;
    @Autowired
    private AuthRequestBiz authRequestBiz;
    @Autowired
    private ThirdServerBiz thirdServerBiz;

    @GetMapping("/{authId}/{serverId}")
    public Response<ThirdAuthVO> redirectUrl(@PathVariable String authId, @PathVariable String serverId) {
        ThirdServer thirdServer = thirdServerBiz.findOne(serverId);
        if (thirdServer == null) {
            throw OAuthError.THIRD_SERVER_NOT_FOUND.exception();
        }

        String redirectUrl = thirdServerBiz.buildAuthorizeUrl(authId, thirdServer, null);
        return ResponseFactory.success(ThirdAuthVO.of(redirectUrl));
    }

    @PostMapping("/login")
    public Response<AuthCodeVO> login(@Valid @RequestBody ThirdLoginRequest request) {
        AuthState authState = authStateBiz.findByState(request.getState());
        if (authState == null) {
            throw OAuthError.AUTH_REQUEST_NOT_FOUND.exception();
        }

        ThirdServer thirdServer = thirdServerBiz.findOne(authState.getThirdServerId());
        if (thirdServer == null) {
            throw OAuthError.THIRD_SERVER_NOT_FOUND.exception();
        }

        AuthService authService = provider.get(thirdServer);
        if (authService == null) {
            throw OAuthError.THIRD_SERVER_NOT_FOUND.exception();
        }

        AuthRequest authRequest = authRequestBiz.findOne(authState.getAuthId());
        if (authRequest == null) {
            throw OAuthError.AUTH_REQUEST_NOT_FOUND.exception();
        }

        Account account = authService.authenticate(thirdServer, request.getCode());
        authRequest.setAuthenticated(true);
        authRequest.setAccountId(account.getId());
        authRequestBiz.save(authRequest);

        // 创建authorisation code
        AuthCode code = authCodeBiz.create(authRequest);

        // 返回code
        return ResponseFactory.success(
                AuthCodeVO.of(
                        authRequest.getId(),
                        code.getId(),
                        authRequest.getState(),
                        authRequest.getRedirectUri()
                )
        );
    }
}
