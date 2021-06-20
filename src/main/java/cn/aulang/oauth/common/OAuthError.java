package cn.aulang.oauth.common;

import cn.aulang.framework.exception.ErrorDefine;
import cn.aulang.framework.exception.annotation.ErrorDeclare;
import cn.aulang.oauth.exception.AccountLockedException;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.exception.CaptchaException;
import cn.aulang.oauth.exception.PasswordExpiredException;

/**
 * 错误代码
 *
 * @author Aulang
 * @date 2021-06-13 21:22
 */
public enum OAuthError implements ErrorDefine {
    @ErrorDeclare(value = 1, group = 10101000, msg = "账号或者密码错误", exception = AuthException.class)
    AUTH_ERROR,
    @ErrorDeclare(value = 2, group = 10101000, msg = "密码已过期，请修改密码", exception = PasswordExpiredException.class)
    PASSWORD_EXPIRED,
    @ErrorDeclare(value = 3, group = 10101000, msg = "账号被锁定，请稍后再试", exception = AccountLockedException.class)
    ACCOUNT_LOCKED,
    @ErrorDeclare(value = 4, group = 10101000, msg = "验证码错误", exception = CaptchaException.class)
    CAPTCHA_ERROR,
    @ErrorDeclare(value = 5, group = 10101000, msg = "client不存在")
    CLIENT_NOT_FOUND,
    @ErrorDeclare(value = 6, group = 10101000, msg = "response_type错误")
    RESPONSE_TYPE_ERROR,
    @ErrorDeclare(value = 7, group = 10101000, msg = "redirect_uri不匹配")
    REDIRECT_URI_ERROR,
    @ErrorDeclare(value = 8, group = 10101000, msg = "scope错误")
    SCOPE_ERROR,
    @ErrorDeclare(value = 9, group = 10101000, msg = "登录请求不存在或者已超时")
    AUTH_REQUEST_NOT_FOUND,
    @ErrorDeclare(value = 10, group = 10101000, msg = "需要授权")
    NEED_APPROVAL,
    @ErrorDeclare(value = 11, group = 10101000, msg = "账号不存在")
    ACCOUNT_NOT_FOUND,
    @ErrorDeclare(value = 12, group = 10101000, msg = "发送验证码失败")
    SEND_CAPTCHA_FAILED,
    @ErrorDeclare(value = 13, group = 10101000, msg = "token已失效")
    TOKEN_EXPIRED,
    @ErrorDeclare(value = 14, group = 10101000, msg = "token不存在")
    TOKEN_NOT_FOUND,
    @ErrorDeclare(value = 15, group = 10101000, msg = "grant_type未授权")
    GRANT_TYPE_UNAUTHORIZED,
    @ErrorDeclare(value = 16, group = 10101000, msg = "登录方式不存在")
    THIRD_SERVER_NOT_FOUND
}
