package cn.aulang.oauth.controller;

import cn.aulang.oauth.jwt.JwtHelper;
import cn.aulang.oauth.thirdserver.core.AuthService;
import cn.aulang.oauth.thirdserver.core.AuthServiceProvider;
import cn.hutool.core.util.RandomUtil;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.LoginPage;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthState;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.exception.ThirdAccountNotExistException;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthStateBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.model.JwtUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.AccountLockedException;

/**
 * @author wulang
 */
@Slf4j
@Controller
@RequestMapping("/third_login")
public class ThirdLoginController {

    private final JwtHelper jwtHelper;
    private final AccountBiz accountBiz;
    private final AuthStateBiz stateBiz;
    private final ThirdServerBiz serverBiz;
    private final AuthRequestBiz requestBiz;
    private final ReturnPageBiz returnPageBiz;
    private final AuthServiceProvider provider;

    @Autowired
    public ThirdLoginController(JwtHelper jwtHelper, AuthStateBiz stateBiz, AccountBiz accountBiz,
                                ThirdServerBiz serverBiz, AuthRequestBiz requestBiz, ReturnPageBiz returnPageBiz,
                                AuthServiceProvider provider) {
        this.provider = provider;
        this.stateBiz = stateBiz;
        this.serverBiz = serverBiz;
        this.jwtHelper = jwtHelper;
        this.requestBiz = requestBiz;
        this.accountBiz = accountBiz;
        this.returnPageBiz = returnPageBiz;
    }

    @GetMapping("/{authorizeId}/{serverId}")
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

    @GetMapping
    public String thirdLogin(@RequestParam(name = "code") String code,
                             @RequestParam(name = "state") String state,
                             Model model) {
        AuthState authState = stateBiz.getByState(state);
        if (authState == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        ThirdServer thirdServer = serverBiz.get(authState.getServerId());
        if (thirdServer == null) {
            return Constants.errorPage(model, "无效的第三方服务");
        }

        AuthService authService = provider.get(thirdServer);
        if (authService == null) {
            return Constants.errorPage(model, "没有第三方服务提供者");
        }

        String authorizeId = authState.getAuthorizeId();
        if (Constants.BIND_STATE_AUTHORIZE_ID.equals(authorizeId)) {
            // 账号绑定
            try {
                authService.bind(thirdServer, code, authState.getAccountId());
                model.addAttribute("name", thirdServer.getName());
                return "bind_success";
            } catch (AuthException e) {
                log.error(thirdServer.getName() + "账号绑定失败", e);
                return Constants.errorPage(model, e.getMessage());
            }
        }

        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        try {
            Account account = authService.authenticate(thirdServer, code);
            request.setAuthenticated(true);
            request.setAccountId(account.getId());
            request = requestBiz.save(request);

            return returnPageBiz.redirect(request, model);
        } catch (ThirdAccountNotExistException e) {
            return returnPageBiz.bindPage(request, model, e.getServerId(), e.getThirdId(), e.getOpenId(), e.getUnionId());
        } catch (Exception e) {
            log.error(thirdServer.getName() + "账号登录失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }

    @GetMapping("/bind/{serverId}")
    public String bind(@PathVariable String serverId,
                       @RequestParam("access_token") String accessToken,
                       Model model) {
        ThirdServer thirdServer = serverBiz.get(serverId);
        if (thirdServer == null) {
            return Constants.errorPage(model, "OAuthServer不存在");
        }

        JwtUser jwtUser;
        try {
            jwtUser = jwtHelper.decode(accessToken);
        } catch (Exception e) {
            return Constants.errorPage(model, "无效的access_token");
        }

        try {
            return serverBiz.buildAuthorizeUrl(
                    Constants.BIND_STATE_AUTHORIZE_ID,
                    thirdServer,
                    jwtUser.getUserId()
            );
        } catch (Exception e) {
            log.error(thirdServer.getName() + "绑定账号失败", e);
            return Constants.errorPage(model, e.getMessage());
        }
    }

    @PostMapping("/bind")
    public String bind(@RequestParam(name = "authorize_id") String authorizeId,
                       @RequestParam(name = "server_id") String serverId,
                       @RequestParam(name = "third_id") String thirdId,
                       @RequestParam(name = "open_id") String openId,
                       @RequestParam(name = "union_id") String unionId,
                       @RequestParam(required = false) String username,
                       @RequestParam(required = false) String password,
                       @RequestParam(required = false) String mobile,
                       @RequestParam(required = false) String captcha,
                       Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "请求已超期失效");
        }

        ThirdServer thirdServer = serverBiz.get(serverId);
        if (thirdServer == null) {
            return Constants.errorPage(request.getLoginPage(), model, "无效的server_id");
        }

        if (StringUtils.isNoneBlank(username, password)) {
            return passwordBind(username, password, captcha, thirdId, openId, unionId, thirdServer, request, model);
        }

        if (StringUtils.isNoneBlank(mobile, captcha)) {
            return captchaBind(mobile, captcha, thirdId, openId, unionId, thirdServer, request, model);
        }

        return Constants.errorPage(request.getLoginPage(), model, "请求参数缺失");
    }

    private String passwordBind(String username, String password, String captcha, String thirdId, String openId, String unionId,
                                ThirdServer thirdServer, AuthRequest request, Model model) {
        if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
            if (request.getCaptcha() != null && !request.getCaptcha().equals(captcha)) {
                model.addAttribute("error", "验证码错误");
                model.addAttribute("username", username);

                return returnPageBiz.bindPage(request, model, thirdServer.getId(), thirdId, openId, unionId);
            }
        }

        String accountId;
        try {
            accountId = accountBiz.login(username, password);
        } catch (AccountLockedException e) {
            return LoginPage.pageOf(request.getLoginPage(), "account_locked");
        }

        if (accountId == null) {
            int triedTimes = request.getTriedTimes() + 1;
            if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
                // 需要验证码了，随机塞个数字就行
                request.setCaptcha(RandomUtil.randomString(4));
            }

            request.setTriedTimes(triedTimes);
            request = requestBiz.save(request);

            // 账号密码错误
            model.addAttribute("error", "账号或密码错误");
            return returnPageBiz.bindPage(request, model, thirdServer.getId(), thirdId, openId, unionId);
        } else {
            request.setAccountId(accountId);
            return returnPageBiz.bindRedirect(request, model, thirdServer, thirdId, openId, unionId);
        }
    }

    private String captchaBind(String mobile, String captcha, String thirdId, String openId, String unionId,
                               ThirdServer thirdServer, AuthRequest request, Model model) {
        if (request.getMobile() != null && !mobile.equals(request.getMobile())) {
            // 手机号码不匹配
            model.addAttribute("error", "手机号码不匹配");
            return returnPageBiz.bindPage(request, model, thirdServer.getId(), thirdId, openId, unionId);
        }

        if (request.getAccountId() == null && !captcha.equals(request.getCaptcha())) {
            // 验证码错误
            model.addAttribute("error", "验证码错误");
            return returnPageBiz.bindPage(request, model, thirdServer.getId(), thirdId, openId, unionId);
        }

        return returnPageBiz.bindRedirect(request, model, thirdServer, thirdId, openId, unionId);
    }
}
