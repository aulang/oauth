package cn.aulang.oauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证请求响应
 *
 * @author Aulang
 * @date 2021-06-19 10:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AuthRequestVO {
    /**
     * 认证请求ID
     */
    private String id;
    /**
     * 需要验证码
     */
    private Boolean needCaptcha;
}
