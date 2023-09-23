package cn.aulang.oauth.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordExpiredException extends AuthException {

    private String accountId;

    public PasswordExpiredException(String msg, String accountId) {
        super(msg);
        this.accountId = accountId;
    }

    public PasswordExpiredException(String msg, Throwable t, String accountId) {
        super(msg, t);
        this.accountId = accountId;
    }
}
