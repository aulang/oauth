package cn.aulang.oauth.controller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:00
 */
@Slf4j
@Controller
public class PasswordController {

    private final AccountBiz accountBiz;
    private final AuthRequestBiz requestBiz;

    @Autowired
    public PasswordController(AccountBiz accountBiz, AuthRequestBiz requestBiz) {
        this.accountBiz = accountBiz;
        this.requestBiz = requestBiz;
    }

    @PostMapping("/change_passwd")
    public String changePwd(@RequestParam(name = "authorize_id") String authorizeId,
                            @RequestParam(name = "password") String password,
                            @RequestParam(name = "confirmed_password") String confirmedPassword,
                            Model model) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request == null) {
            return Constants.errorPage(model, "登录请求不存在或已失效");
        }

        if (!password.equals(confirmedPassword)) {
            model.addAttribute("error", "两次密码不一致");
            return "change_passwd";
        }

        String accountId = request.getAccountId();
        if (!request.isAuthenticated() || accountId == null) {
            return Constants.errorPage(model, "用户未登录");
        }

        try {
            String result = accountBiz.changePassword(accountId, password, false);
            if (result == null) {
                return Constants.errorPage(model, "账号不存在");
            }
            request.setAccountId(null);
            request.setAuthenticated(false);
            requestBiz.save(request);
        } catch (Exception e) {
            log.error("修改密码失败", e);
            return Constants.errorPage(model, e.getMessage());
        }

        model.addAttribute("authorizeId", authorizeId);
        return "change_success";
    }
}
