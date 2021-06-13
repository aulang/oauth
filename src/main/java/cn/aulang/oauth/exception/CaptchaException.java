package cn.aulang.oauth.exception;


/**
 * 验证码错误异常类
 *
 * @author Aulang
 */
public class CaptchaException extends AuthException {
    public CaptchaException() {
    }

    public CaptchaException(Integer code, String msg) {
        super(code, msg);
    }

    public CaptchaException(Integer code, String msg, String debug) {
        super(code, msg, debug);
    }

    public CaptchaException(Integer code, String msg, String debug, String i18nKey) {
        super(code, msg, debug, i18nKey);
    }

    public CaptchaException(Integer code, String msg, String debug, String i18nKey, Throwable cause) {
        super(code, msg, debug, i18nKey, cause);
    }
}
