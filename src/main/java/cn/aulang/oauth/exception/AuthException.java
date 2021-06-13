package cn.aulang.oauth.exception;

import cn.aulang.framework.exception.BaseException;

/**
 * 认证失败异常类
 *
 * @author Aulang
 */
public class AuthException extends BaseException {
    public AuthException() {
    }

    public AuthException(Integer code, String msg) {
        super(code, msg);
    }

    public AuthException(Integer code, String msg, String debug) {
        super(code, msg, debug);
    }

    public AuthException(Integer code, String msg, String debug, String i18nKey) {
        super(code, msg, debug, i18nKey);
    }

    public AuthException(Integer code, String msg, String debug, String i18nKey, Throwable cause) {
        super(code, msg, debug, i18nKey, cause);
    }
}
