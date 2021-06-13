package cn.aulang.oauth.exception;


import lombok.Data;

/**
 * 密码过期错误异常类
 *
 * @author Aulang
 */
@Data
public class PasswordExpiredException extends AuthException {
    private String accountId;

    public PasswordExpiredException() {
    }

    public PasswordExpiredException(Integer code, String msg) {
        super(code, msg);
    }

    public PasswordExpiredException(Integer code, String msg, String debug) {
        super(code, msg, debug);
    }

    public PasswordExpiredException(Integer code, String msg, String debug, String i18nKey) {
        super(code, msg, debug, i18nKey);
    }

    public PasswordExpiredException(Integer code, String msg, String debug, String i18nKey, Throwable cause) {
        super(code, msg, debug, i18nKey, cause);
    }

    public PasswordExpiredException accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }
}
