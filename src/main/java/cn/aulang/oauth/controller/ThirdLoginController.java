package cn.aulang.oauth.controller;

import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.thirdserver.core.AuthService;
import cn.aulang.oauth.thirdserver.core.AuthServiceProvider;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthStateBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.model.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wulang
 */
@Slf4j
@Controller
public class ThirdLoginController {

    private final JwtHelper jwtHelper;
    private final AuthStateBiz stateBiz;
    private final ThirdServerBiz serverBiz;
    private final AuthRequestBiz requestBiz;
    private final ReturnPageBiz returnPageBiz;
    private final AuthServiceProvider provider;

    @Autowired
    public ThirdLoginController(JwtHelper jwtHelper, AuthStateBiz stateBiz, ThirdServerBiz serverBiz,
                                AuthRequestBiz requestBiz, ReturnPageBiz returnPageBiz, AuthServiceProvider provider) {
        this.jwtHelper = jwtHelper;
        this.stateBiz = stateBiz;
        this.serverBiz = serverBiz;
        this.requestBiz = requestBiz;
        this.returnPageBiz = returnPageBiz;
        this.provider = provider;
    }

    @GetMapping("/third_login/{authorizeId}/{serverId}")
    public String redirectAuthorizeUrl(@PathVariable String authorizeId,
                                       @PathVariable String serverId,
                                       Model model) {
        ThirdServer thirdServer = serverBiz.get(serverId);
        if (thirdServer == null) {
            return Constants.errorPage(model, "OAuthServer不存在");
        }
        try {
            return serverBiz.buildAuthorizeUrl(authorizeId, thirdServer, null);
        } catch (Exception e) {
            log.error(thirdServer.getName() + "登录失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }

    @GetMapping("/third_login")
    public String otherLogin(@RequestParam(name = "code") String code,
                             @RequestParam(name = "state") String state,
                             Model model) {
        AuthState authState = stateBiz.getByState(state);
        if (authState == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        ThirdServer server = serverBiz.get(authState.getThirdServerId());
        if (server == null) {
            return Constants.errorPage(model, "无效的第三方服务");
        }

        AuthService authService = provider.get(server);
        if (authService == null) {
            return Constants.errorPage(model, "没有第三方服务提供者");
        }

        String authorizeId = authState.getAuthorizeId();
        if (Constants.BIND_STATE_AUTHORIZE_ID.equals(authorizeId)) {
            // 账号绑定
            try {
                authService.bind(server, code, authState.getAccountId());
                model.addAttribute("name", server.getName());
                return "bind_success";
            } catch (AuthException e) {
                log.error(server.getName() + "账号绑定失败", e);
                return Constants.errorPage(model, e.getMessage());
            }
        }

        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        try {
            Account account = authService.authenticate(server, code);
            request.setAuthenticated(true);
            request.setAccountId(account.getId());
            request = requestBiz.save(request);

            return returnPageBiz.redirect(request, model);
        } catch (AuthException e) {
            log.error(server.getName() + "账号登录失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }

    @GetMapping("/third_login/bind/{name}")
    public String bind(@PathVariable String name,
                       @RequestParam("access_token") String accessToken,
                       Model model) {
        ThirdServer thirdServer = serverBiz.findByName(name);
        if (thirdServer == null) {
            return Constants.errorPage(model, "OAuthServer不存在");
        }

        Profile profile;
        try {
            profile = jwtHelper.decode(accessToken);
        } catch (Exception e) {
            return Constants.errorPage(model, "无效的access_token");
        }

        try {
            return serverBiz.buildAuthorizeUrl(
                    Constants.BIND_STATE_AUTHORIZE_ID,
                    thirdServer,
                    profile.getId()
            );
        } catch (Exception e) {
            log.error(thirdServer.getName() + "绑定账号失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }
}
