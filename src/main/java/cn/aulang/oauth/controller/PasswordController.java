package cn.aulang.oauth.controller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ReturnPageBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wulang
 */
@Slf4j
@Controller
public class PasswordController {

    private final AccountBiz accountBiz;
    private final AuthRequestBiz requestBiz;
    private final ReturnPageBiz returnPageBiz;

    @Autowired
    public PasswordController(AccountBiz accountBiz, AuthRequestBiz requestBiz, ReturnPageBiz returnPageBiz) {
        this.accountBiz = accountBiz;
        this.requestBiz = requestBiz;
        this.returnPageBiz = returnPageBiz;
    }

    @PostMapping("/change_passwd")
    public String changePwd(@RequestParam(name = "authorize_id") String authorizeId,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "repassword") String repassword,
                            Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (!password.equals(repassword)) {
            model.addAttribute("error", "两次密码不一致");
            return "change_passwd";
        }

        String accountId = request.getAccountId();
        if (!request.getAuthenticated() || accountId == null) {
            return Constants.errorPage(model, "用户未登录");
        }

        try {
            String result = accountBiz.changePwd(accountId, password);
            if (result == null) {
                return Constants.errorPage(model, "账号不存在");
            }
            request.setAccountId(null);
            request.setAuthenticated(false);
            request.setMustChpwd(false);
            request.setChpwdReason(null);
            requestBiz.save(request);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Constants.errorPage(model, e.getMessage());
        }

        model.addAttribute("authorizeId", authorizeId);
        return "change_success";
    }

    @GetMapping("/forget_passwd/{authorizeId}")
    public String forgetPwd(@PathVariable("authorizeId") String authorizeId, Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        return returnPageBiz.forgetPwdPage(request, model);
    }

    @PostMapping("/forget_passwd")
    public String forgetPwd(@RequestParam(name = "authorize_id") String authorizeId,
                            @RequestParam(name = "mobile") String mobile,
                            @RequestParam(name = "captcha") String captcha,
                            Model model) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (request.getMobile() != null && !mobile.equals(request.getMobile())) {
            model.addAttribute("error", "手机号码不匹配");
            return returnPageBiz.forgetPwdPage(request, model);
        }

        if (request.getAccountId() == null && !captcha.equals(request.getCaptcha())) {
            model.addAttribute("error", "验证码错误");
            return returnPageBiz.forgetPwdPage(request, model);
        }

        request.setAuthenticated(true);
        request.setMustChpwd(true);
        requestBiz.save(request);

        model.addAttribute("authorizeId", authorizeId);
        return "change_passwd";
    }
}
