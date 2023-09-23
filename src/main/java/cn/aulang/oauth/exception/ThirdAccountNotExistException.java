package cn.aulang.oauth.exception;

import lombok.Getter;

/**
 * @author wulang
 */
@Getter
public class ThirdAccountNotExistException extends AuthException {

    private final String serverId;
    private final String thirdId;
    private final String openId;
    private final String unionId;

    public ThirdAccountNotExistException(String serverId, String thirdId, String openId, String unionId) {
        super("第三方账号不存在");
        this.serverId = serverId;
        this.thirdId = thirdId;
        this.openId = openId;
        this.unionId = unionId;
    }
}
