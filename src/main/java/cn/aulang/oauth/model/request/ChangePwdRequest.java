package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 修改密码请求
 *
 * @author Aulang
 * @date 2021-06-20 12:15
 */
@Data
public class ChangePwdRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    @NotBlank(message = "密码不能为空")
    private String password;
}
