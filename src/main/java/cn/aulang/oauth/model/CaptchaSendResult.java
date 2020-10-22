package cn.aulang.oauth.model;

import lombok.Data;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 11:32
 */
@Data
public class CaptchaSendResult {
    /**
     * 登录认证请求ID
     */
    private String requestId;
    /**
     * 发送账号ID
     */
    private String accountId;
    /**
     * 发送目标
     */
    private String target;

    public CaptchaSendResult() {
    }

    public CaptchaSendResult(String requestId, String accountId, String target) {
        this.requestId = requestId;
        this.accountId = accountId;
        this.target = target;
    }
}
