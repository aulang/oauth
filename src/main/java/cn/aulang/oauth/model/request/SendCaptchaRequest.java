package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发生严重码请求
 *
 * @author Aulang
 * @date 2021-06-19 21:07
 */
@Data
public class SendCaptchaRequest {
    /**
     * 认证请求ID
     */
    private String authId;
    /**
     * 客户端ID
     */
    private String clientId;

    @NotBlank(message = "手机号不能为空")
    private String mobile;
}
