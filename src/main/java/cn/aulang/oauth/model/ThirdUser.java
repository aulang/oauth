package cn.aulang.oauth.model;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 第三方用户
 *
 * @author wulang
 */
@Data
public class ThirdUser {

    @NotBlank
    private String clientId;
    @NotBlank
    private String serverId;
    @NotBlank
    private String thirdId;
    private String openId;
    private String unionId;

    private String username;
    private String password;

    private String authorizeId;
    private String mobile;
    private String captcha;
}
