package cn.aulang.oauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 第三方认证响应
 *
 * @author Aulang
 * @date 2021-06-20 17:29
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ThirdAuthVO {

    private String redirectUrl;

}
