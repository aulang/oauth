package cn.aulang.oauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 认证Code
 *
 * @author Aulang
 * @date 2021-06-19 17:00
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AuthCodeVO {
    private String authId;
    private String code;
    private String state;
    private String redirectUri;
}
