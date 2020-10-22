package cn.aulang.oauth.controller;

import lombok.extern.slf4j.Slf4j;
import cn.aulang.oauth.auth.AuthServiceProvider;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthStateBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.server.core.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:06
 */
@Slf4j
@Controller
public class ThirdLoginController {
    @Autowired
    private AuthStateBiz stateBiz;
    @Autowired
    private ThirdServerBiz serverBiz;
    @Autowired
    private AccountTokenBiz tokenBiz;
    @Autowired
    private AuthRequestBiz requestBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;
    @Autowired
    private AuthServiceProvider provider;

    @GetMapping("/third_login/{authorizeId}/{serverId}")
    public String redirectAuthorizeUrl(@PathVariable String authorizeId,
                                       @PathVariable String serverId,
                                       Model model) {
        ThirdServer server = serverBiz.findOne(serverId);
        if (server == null) {
            return Constants.errorPage(model, "OAuthServer不存在");
        }
        return serverBiz.buildAuthorizeUrl(authorizeId, server, null);
    }

    @GetMapping("/third_login")
    public String otherLogin(@RequestParam(name = "code") String code,
                             @RequestParam(name = "state") String state,
                             HttpServletResponse response, Model model) {
        AuthState authState = stateBiz.findByState(state);
        if (authState == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        ThirdServer server = serverBiz.findOne(authState.getThirdServerId());
        if (server == null) {
            return Constants.errorPage(model, "无效的第三方服务");
        }

        AuthService authService = provider.get(server);
        if (authService == null) {
            return Constants.errorPage(model, "没有第三方服务提供者");
        }

        String authorizeId = authState.getAuthorizeId();
        if (Constants.BIND_STATE_AUTHORIZE_ID.equals(authorizeId)) {
            /**
             * 账号绑定
             */
            try {
                authService.bind(server, code, authState.getAccountId());
                model.addAttribute("name", server.getName());
                return "bind_success";
            } catch (AuthException e) {
                log.error(server.getName() + "账号绑定失败", e);
                return Constants.errorPage(model, e.getMessage());
            }
        }

        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        try {
            Account account = authService.authenticate(server, code);
            request.setAuthenticated(true);
            request.setAccountId(account.getId());
            request = requestBiz.save(request);

            return returnPageBiz.approvalPage(request, response, model);
        } catch (AuthException e) {
            log.error(server.getName() + "账号登录失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }

    @GetMapping("/third_login/bind/{name}")
    public String bind(@PathVariable String name,
                       @RequestParam("access_token") String accessToken,
                       Model model) {
        ThirdServer server = serverBiz.findByName(name);
        if (server == null) {
            return Constants.errorPage(model, "OAuthServer不存在");
        }

        AccountToken accountToken = tokenBiz.findByAccessToken(accessToken);
        if (accountToken == null) {
            return Constants.errorPage(model, "登录信息无效");
        }

        return serverBiz.buildAuthorizeUrl(
                Constants.BIND_STATE_AUTHORIZE_ID,
                server,
                accountToken.getAccountId()
        );
    }
}
