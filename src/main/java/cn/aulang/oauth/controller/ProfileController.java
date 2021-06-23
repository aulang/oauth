package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.model.bo.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Profile控制器
 *
 * @author Aulang
 * @date 2021-06-20 17:05
 */
@RestController
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AccountTokenBiz accountTokenBiz;

    @GetMapping("")
    public Response<Profile> profile(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        AccountToken accountToken = accountTokenBiz.findByAuthorization(authorization);
        Profile profile = accountBiz.getProfile(accountToken.getAccountId());
        return ResponseFactory.success(profile);
    }
}
