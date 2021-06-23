package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.exception.AccountLockedException;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.model.request.CaptchaLoginRequest;
import cn.aulang.oauth.model.request.LoginRequest;
import cn.aulang.oauth.model.request.SsoRequest;
import cn.aulang.oauth.model.response.AuthCodeVO;
import cn.aulang.oauth.model.response.AuthRequestVO;
import cn.aulang.oauth.property.LoginProperties;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 登录控制器
 *
 * @author Aulang
 * @date 2021-06-19 11:23
 */
@RestController
@RequestMapping("/login")
@EnableConfigurationProperties(LoginProperties.class)
public class LoginController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private AuthCodeBiz authCodeBiz;
    @Autowired
    private AuthRequestBiz authRequestBiz;
    @Autowired
    private LoginProperties loginProperties;

    @PostMapping("")
    public Response<?> login(@Valid @RequestBody LoginRequest request) {
        String authId = request.getAuthId();

        // 登录请求是否存在
        AuthRequest authRequest = authRequestBiz.getAuthRequest(authId);

        // 秘密错误次数需要验证码
        if (authRequest.getTriedTimes() > loginProperties.getNeedCaptchaTimes()
                && !authRequest.getCaptcha().equalsIgnoreCase(request.getCaptcha())) {
            throw OAuthError.CAPTCHA_ERROR.exception();
        }

        try {
            // 登录
            Account account = accountBiz.login(request.getUsername(), request.getPassword());

            authRequest.setAccountId(account.getId());
            authRequest.setAuthenticated(true);
            authRequestBiz.save(authRequest);
        } catch (PasswordExpiredException e) {
            // 密码过期，需要修改密码
            authRequest.setAuthenticated(true);
            authRequest.setAccountId(e.getAccountId());
            authRequestBiz.save(authRequest);
            return ResponseFactory.build(e.getCode(), e.getMsg(), AuthRequestVO.of(authId, false));
        } catch (AccountLockedException e) {
            // 账号锁定
            return ResponseFactory.build(e.getCode(), e.getMsg(), AuthRequestVO.of(authId, false));
        } catch (AuthException e) {
            // 账号或密码错误
            int triedTimes = authRequest.getTriedTimes() + 1;

            boolean needCaptcha = triedTimes > loginProperties.getNeedCaptchaTimes();
            if (needCaptcha) {
                // 需要验证码了，随机塞个数字就行
                request.setCaptcha(RandomUtil.randomString(4));
            }

            authRequest.setTriedTimes(triedTimes);
            authRequestBiz.save(authRequest);

            return ResponseFactory.build(e.getCode(), e.getMsg(), AuthRequestVO.of(authId, needCaptcha));
        }

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

    @PostMapping("/sso")
    public Response<AuthCodeVO> sso(@Valid @RequestBody SsoRequest request) {
        String authId = request.getAuthId();
        // 判断是否登录过
        authRequestBiz.checkAuthenticated(authId);

        // 创建authorisation code
        AuthCode code = authCodeBiz.create(authId, true);

        // 返回code
        return ResponseFactory.success(
                AuthCodeVO.of(
                        authId,
                        code.getId(),
                        request.getState(),
                        request.getRedirectUri()
                )
        );
    }

    @PostMapping("/captcha")
    public Response<AuthCodeVO> captcha(@Valid @RequestBody CaptchaLoginRequest request) {
        String authId = request.getAuthId();
        AuthRequest authRequest = authRequestBiz.getAuthRequest(authId);

        if (!StrUtil.equals(request.getMobile(), authRequest.getMobile())
                || !StrUtil.equals(request.getCaptcha(), authRequest.getCaptcha())) {
            throw OAuthError.CAPTCHA_ERROR.exception();
        }

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
