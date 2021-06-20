package cn.aulang.oauth.model.bo;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AccountToken;
import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 16:32
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class AccessToken {
    private String token_type = "Bearer";
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private String scope;

    public static AccessToken build(AccountToken token) {
        return of(
                "Bearer",
                token.getAccessToken(),
                token.getExpiresIn(),
                token.getRefreshToken(),
                CollUtil.join(token.getScopes(), Constants.COMMA)
        );
    }
}
