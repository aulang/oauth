package cn.aulang.oauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 验证码发送响应
 *
 * @author Aulang
 * @date 2021-06-19 21:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SendCaptchaVO {
    private String authId;
    private String target;
}
