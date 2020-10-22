package cn.aulang.oauth.model;

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
@AllArgsConstructor
public class AccessToken {
    private String access_token;
    private String refresh_token;
    private int expires_in;

    public static AccessToken create(String access_token, String refresh_token, int expires_in) {
        return new AccessToken(access_token, refresh_token, expires_in);
    }
}
