package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 单点认证请求
 *
 * @author Aulang
 * @date 2021-06-17 23:00
 */
@Data
public class SsoRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    private String state;
    private String redirectUri;
}
