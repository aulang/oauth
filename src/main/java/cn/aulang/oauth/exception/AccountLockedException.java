package cn.aulang.oauth.exception;

/**
 * 账号锁定异常
 *
 * @author Aulang
 * @date 2021-06-19 11:29
 */
public class AccountLockedException extends AuthException {
    private String accountId;

    public AccountLockedException() {
    }

    public AccountLockedException(Integer code, String msg) {
        super(code, msg);
    }

    public AccountLockedException(Integer code, String msg, String debug) {
        super(code, msg, debug);
    }

    public AccountLockedException(Integer code, String msg, String debug, String i18nKey) {
        super(code, msg, debug, i18nKey);
    }

    public AccountLockedException(Integer code, String msg, String debug, String i18nKey, Throwable cause) {
        super(code, msg, debug, i18nKey, cause);
    }

    public AccountLockedException accountId(String accountId) {
        this.accountId = accountId;
        return this;
    }
}