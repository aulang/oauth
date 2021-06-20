package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 第三方登录请求
 *
 * @author Aulang
 * @date 2021-06-20 17:34
 */
@Data
public class ThirdLoginRequest {
    @NotBlank(message = "code不能为空")
    private String code;
    @NotBlank(message = "state不能为空")
    private String state;
}
