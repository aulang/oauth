package cn.aulang.oauth.controller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.LoginPage;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;

/**
 * @author wulang
 */
@Controller
public class LoginController {

    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final AuthRequestBiz requestBiz;
    private final ReturnPageBiz returnPageBiz;

    public LoginController(ClientBiz clientBiz, AccountBiz accountBiz, AuthRequestBiz requestBiz, ReturnPageBiz returnPageBiz) {
        this.clientBiz = clientBiz;
        this.accountBiz = accountBiz;
        this.requestBiz = requestBiz;
        this.returnPageBiz = returnPageBiz;
    }

    @GetMapping("/login/{authorizeId}")
    public String login(@PathVariable String authorizeId, Model model) {
        AuthRequest request = requestBiz.get(authorizeId);

        if (request == null) {
            return Constants.errorPage(model, "登录认证请求不存在或已失效");
        }

        if (request.getAuthenticated()) {
            if (request.getMustChpwd() != null && request.getMustChpwd()) {
                returnPageBiz.changePwdPage(request, model);
            }

            // 已经登录，到授权页面
            return returnPageBiz.redirect(request, model);
        } else {
            // 未登录，到登录页面
            return returnPageBiz.loginPage(request, null, model);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "username") String username,
                        @RequestParam(name = "password") String password,
                        @RequestParam(name = "captcha", required = false) String captcha,
                        Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        Client client = clientBiz.get(request.getClientId());
        if (client == null) {
            return Constants.errorPage(request.getLoginPage(), model, "无效的client_id");
        }

        if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
            if (request.getCaptcha() != null && !request.getCaptcha().equals(captcha)) {
                model.addAttribute("error", "验证码错误");
                model.addAttribute("username", username);
                return returnPageBiz.loginPage(request, client, model);
            }
        }

        try {
            String accountId = accountBiz.login(username, password);
            if (accountId != null) {
                request.setAccountId(accountId);
                request.setAuthenticated(true);
                request = requestBiz.save(request);
                return returnPageBiz.redirect(request, model);
            } else {
                model.addAttribute("error", "账号或密码错误");
            }
        } catch (AccountLockedException e) {
            return LoginPage.pageOf(request.getLoginPage(), "account_locked");
        } catch (PasswordExpiredException e) {
            String reason = e.getMessage();

            request.setMustChpwd(true);
            request.setAuthenticated(true);
            request.setAccountId(e.getAccountId());
            request.setChpwdReason(reason);
            requestBiz.save(request);

            model.addAttribute("error", reason);
            model.addAttribute("authorizeId", authorizeId);
            return LoginPage.pageOf(request.getLoginPage(), "change_passwd");
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        int triedTimes = request.getTriedTimes() + 1;
        if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
            // 需要验证码了，随机塞个数字就行
            request.setCaptcha(RandomUtil.randomString(4));
        }

        request.setTriedTimes(triedTimes);
        request = requestBiz.save(request);

        model.addAttribute("username", username);
        return returnPageBiz.loginPage(request, client, model);
    }

    /**
     * 使用验证码登录
     */
    @PostMapping("/login/captcha")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "mobile") String mobile,
                        @RequestParam(name = "captcha") String captcha,
                        Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (request.getMobile() != null && !mobile.equals(request.getMobile())) {
            model.addAttribute("error", "手机号码不匹配");
            return returnPageBiz.loginPage(request, null, model);
        }

        if (request.getAccountId() == null && !captcha.equals(request.getCaptcha())) {
            model.addAttribute("error", "验证码错误");
            return returnPageBiz.loginPage(request, null, model);
        }

        request.setAuthenticated(true);
        request = requestBiz.save(request);

        return returnPageBiz.redirect(request, model);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "redirect_uri", required = false) String redirectUri,
                       @CookieValue(name = Constants.SSO_COOKIE_NAME, required = false) String authorizeId,
                       HttpServletResponse response) throws IOException {
        if (authorizeId != null) {
            requestBiz.delete(authorizeId);
        }
        Constants.removeSsoCookie(response);
        response.sendRedirect(redirectUri != null ? redirectUri : "/");
    }
}
