package cn.aulang.oauth.model.bo;

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
public class CaptchaSendResult {
    /**
     * 发送账号ID
     */
    private String accountId;
    /**
     * 发送目标，邮箱或手机
     */
    private String target;
    /**
     * 验证码
     */
    private String captcha;
    /**
     * 发生内容
     */
    private String content;
}
