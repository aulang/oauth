package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 11:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SendCaptchaResult {
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
}
