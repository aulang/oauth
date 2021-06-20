package cn.aulang.oauth.model.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 授权请求
 *
 * @author Aulang
 * @date 2021-06-20 15:54
 */
@Data
public class ApprovalRequest {
    @NotBlank(message = "认证ID不能为空")
    private String authId;
    @NotNull(message = "授权scope不能为空")
    private List<String> approved;
}
