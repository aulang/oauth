package cn.aulang.oauth.controller;

import cn.aulang.framework.exception.CommonError;
import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.auth.AuthServiceProvider;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthStateBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.model.bo.Server;
import cn.aulang.oauth.model.request.ThirdBindRequest;
import cn.aulang.oauth.model.request.ThirdLoginRequest;
import cn.aulang.oauth.model.response.AuthCodeVO;
import cn.aulang.oauth.model.response.ThirdAuthVO;
import cn.aulang.oauth.model.response.ThirdServersVO;
import cn.aulang.oauth.server.core.AuthService;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * 第三方登录控制器
 *
 * @author Aulang
 * @date 2021-06-20 17:17
 */
@RestController
@RequestMapping("/third")
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
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    @GetMapping("/servers")
    public Response<ThirdServersVO> servers() {
        List<Server> servers = thirdServerBiz.getAllServers();
        return ResponseFactory.success(ThirdServersVO.of(servers));
    }

    @GetMapping("/login/{authId}/{serverId}")
    public Response<ThirdAuthVO> redirectUrl(@PathVariable("authId") String authId,
                                             @PathVariable("serverId") String serverId) {
        ThirdServer thirdServer = thirdServerBiz.getThirdServer(serverId);
        String redirectUrl = thirdServerBiz.buildAuthorizeUrl(authId, thirdServer, null);
        return ResponseFactory.success(ThirdAuthVO.of(redirectUrl));
    }

    @PostMapping("/login")
    public Response<AuthCodeVO> login(@Valid @RequestBody ThirdLoginRequest request) {
        AuthState authState = authStateBiz.getAuthState(request.getState());

        ThirdServer thirdServer = thirdServerBiz.getThirdServer(authState.getThirdServerId());

        AuthRequest authRequest = authRequestBiz.getAuthRequest(authState.getAuthId());

        AuthService authService = provider.get(thirdServer);

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

    @GetMapping("/bind/{serverId}")
    public Response<ThirdAuthVO> bind(
            @PathVariable("serverId") String serverId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        ThirdServer thirdServer = thirdServerBiz.getThirdServer(serverId);
        if (thirdServer == null) {
            throw OAuthError.THIRD_SERVER_NOT_FOUND.exception();
        }

        AccountToken accountToken = accountTokenBiz.findByAuthorization(authorization);

        String redirectUrl = thirdServerBiz.buildAuthorizeUrl(
                Constants.BIND_AUTH_ID,
                thirdServer,
                accountToken.getAccountId()
        );

        return ResponseFactory.success(ThirdAuthVO.of(redirectUrl));
    }

    @PostMapping("/bind")
    public Response<?> bind(@Valid @RequestBody ThirdBindRequest request) {
        AuthRequest authRequest = authRequestBiz.getAuthRequest(request.getAuthId());
        AuthState authState = authStateBiz.getAuthState(request.getState());

        if (StrUtil.hasBlank(authRequest.getAccountId(), authState.getAccountId())
                || !authRequest.getAccountId().equals(authState.getAccountId())) {
            // 登录账号和绑定账号不一致
            throw CommonError.BAD_REQUEST.exception();
        }

        ThirdServer thirdServer = thirdServerBiz.getThirdServer(authState.getThirdServerId());

        AuthService authService = provider.get(thirdServer);

        authService.bind(thirdServer, request.getCode(), authState.getAccountId());

        return ResponseFactory.success();
    }
}
