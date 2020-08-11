package net.aulang.oauth.controller;

import cn.hutool.core.util.RandomUtil;
import net.aulang.oauth.common.Constants;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.exception.PasswordExpiredException;
import net.aulang.oauth.manage.AccountBiz;
import net.aulang.oauth.manage.AuthRequestBiz;
import net.aulang.oauth.manage.ClientBiz;
import net.aulang.oauth.manage.ReturnPageBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.security.auth.login.AccountLockedException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/6 11:01
 */
@Controller
public class LoginController {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AuthRequestBiz requestBiz;
    @Autowired
    private ReturnPageBiz returnPageBiz;

    @GetMapping("/login/{authorizeId}")
    public String login(@PathVariable String authorizeId, HttpServletResponse response, Model model) {
        AuthRequest request = requestBiz.findOne(authorizeId);

        if (request == null) {
            return Constants.errorPage(model, "登录认证请求不存在或已失效");
        }

        if (request.isAuthenticated()) {
            /**
             * 已经登录，到授权页面
             */
            return returnPageBiz.approvalPage(request, response, model);
        } else {
            /**
             * 未登录，到登录页面
             */
            return returnPageBiz.loginPage(request, null, model);
        }
    }

    @PostMapping("/login")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "username") String username,
                        @RequestParam(name = "password") String password,
                        @RequestParam(name = "captcha", required = false) String captcha,
                        HttpServletResponse response,
                        Model model) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        Client client = clientBiz.findOne(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的client_id");
        }

        if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
            if (request.getCaptcha() != null && !request.getCaptcha().equals(captcha)) {
                model.addAttribute("error", "验证码错误");
                return returnPageBiz.loginPage(request, client, model);
            }
        }

        try {
            String accountId = accountBiz.login(username, password);
            if (accountId != null) {
                request.setAccountId(accountId);
                request.setAuthenticated(true);
                request = requestBiz.save(request);
                return returnPageBiz.approvalPage(request, response, model);
            } else {
                model.addAttribute("error", "账号或密码错误");
            }
        } catch (AccountLockedException e) {
            return "account_locked";
        } catch (PasswordExpiredException e) {
            request.setAuthenticated(true);
            request.setAccountId(e.getAccountId());
            requestBiz.save(request);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("authorizeId", authorizeId);
            return "change_passwd";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }


        int triedTimes = request.getTriedTimes() + 1;
        if (request.getTriedTimes() > Constants.NEED_CAPTCHA_TIMES) {
            /**
             * 需要验证码了，随机塞个数字就行
             */
            request.setCaptcha(RandomUtil.randomString(4));
        }

        request.setTriedTimes(triedTimes);
        request = requestBiz.save(request);

        return returnPageBiz.loginPage(request, client, model);
    }

    /**
     * 使用验证码登录
     */
    @PostMapping("/login/captcha")
    public String login(@RequestParam(name = "authorize_id") String authorizeId,
                        @RequestParam(name = "mobile") String mobile,
                        @RequestParam(name = "captcha") String captcha,
                        HttpServletResponse response, Model model) {
        AuthRequest request = requestBiz.findOne(authorizeId);
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

        return returnPageBiz.approvalPage(request, response, model);
    }

    @GetMapping("/logout")
    public void logout(@RequestParam(name = "redirect_url", required = false) String redirectUrl,
                       HttpServletResponse response) throws IOException {
        response.addCookie(Constants.removeSsoCookie());
        response.sendRedirect(redirectUrl != null ? redirectUrl : "/");
    }
}
