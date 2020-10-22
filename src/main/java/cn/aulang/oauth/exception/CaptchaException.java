package cn.aulang.oauth.exception;


public class CaptchaException extends AuthException {
    public CaptchaException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public CaptchaException(String msg) {
        super(msg);
    }
}
