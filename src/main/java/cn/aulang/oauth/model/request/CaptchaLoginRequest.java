package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 验证码登录请求
 *
 * @author Aulang
 * @date 2021-06-22 23:16
 */
@Data
public class CaptchaLoginRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    @NotBlank(message = "手机号不能为空")
    private String mobile;
    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
