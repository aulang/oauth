package cn.aulang.oauth.common;

import cn.aulang.framework.exception.ErrorDefine;
import cn.aulang.framework.exception.annotation.ErrorDeclare;
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
    @ErrorDeclare(value = 2, group = 10101000, msg = "验证码错误", exception = CaptchaException.class)
    CAPTCHA_ERROR,
    @ErrorDeclare(value = 3, group = 10101000, msg = "密码过期", exception = PasswordExpiredException.class)
    PASSWORD_EXPIRED
}
