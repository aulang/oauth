package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Aulang
 * @date 2021-06-22 15:34
 */
@Data
public class ThirdBindRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    @NotBlank(message = "code不能为空")
    private String code;
    @NotBlank(message = "state不能为空")
    private String state;
}
