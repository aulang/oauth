package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 验证码认证请求
 *
 * @author Aulang
 * @date 2021-06-17 23:00
 */
@Data
public class LoginRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    /**
     * 验证码
     */
    private String captcha;
}
