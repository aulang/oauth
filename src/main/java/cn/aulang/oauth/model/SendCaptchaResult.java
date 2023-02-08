package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SendCaptchaResult {
    /**
     * 登录认证请求ID
     */
    private String authorizeId;
    /**
     * 发送账号ID
     */
    private String accountId;
    /**
     * 发送目标
     */
    private String target;
}
