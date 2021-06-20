package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 认证请求
 *
 * @author Aulang
 * @date 2021-06-17 21:11
 */
@Data
public class AuthorizeRequest {
    @NotBlank(message = "客户端ID不允许为空")
    private String clientId;
    @NotBlank(message = "授权模式不能为空")
    private String responseType;
    @NotBlank(message = "重定向地址不能为空")
    private String redirectUri;
    @NotBlank(message = "Code Challenge不能为空")
    private String codeChallenge;

    private String scope;
    private String state;
}
