package cn.aulang.oauth.exception;

public class PasswordExpiredException extends AuthException {

    private String accountId;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public PasswordExpiredException(String msg, String accountId) {
        super(msg);
        this.accountId = accountId;
    }

    public PasswordExpiredException(String msg, Throwable t, String accountId) {
        super(msg, t);
        this.accountId = accountId;
    }
}
