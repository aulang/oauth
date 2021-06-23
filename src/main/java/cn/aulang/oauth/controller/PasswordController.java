package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.model.request.ChangePwdRequest;
import cn.aulang.oauth.model.response.AuthCodeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 密码控制器
 *
 * @author Aulang
 * @date 2021-06-20 12:09
 */
@RestController
@RequestMapping("/password")
public class PasswordController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AuthCodeBiz authCodeBiz;
    @Autowired
    private AuthRequestBiz authRequestBiz;

    @PostMapping("/change")
    public Response<?> change(@Valid @RequestBody ChangePwdRequest request) {
        String authId = request.getAuthId();

        // 检查是否已认证
        AuthRequest authRequest = authRequestBiz.checkAuthenticated(authId);

        // 修改密码
        accountBiz.changePassword(authRequest.getAccountId(), request.getPassword(), false);

        // 创建authorisation code
        AuthCode code = authCodeBiz.create(authRequest);

        // 返回code
        return ResponseFactory.success(
                AuthCodeVO.of(
                        authId,
                        code.getId(),
                        authRequest.getState(),
                        authRequest.getRedirectUri()
                )
        );
    }
}
