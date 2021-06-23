package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 获取令牌请求
 *
 * @author Aulang
 * @date 2021-06-19 23:00
 */
@Data
public class TokenRequest {
    @NotBlank(message = "client id不能为空")
    private String clientId;
    @NotBlank(message = "grantType不能为空")
    private String grantType;

    private String code;
    private String redirectUri;
    private String codeVerifier;

    private String refreshToken;
    private String clientSecret;

    private String authId;
    private String mobile;
    private String captcha;
}
