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

    private String accessToken;
    private String refreshToken;
    private int expiresIn;

    public static AccessToken create(String accessToken, String refreshToken, int expiresIn) {
        return new AccessToken(accessToken, refreshToken, expiresIn);
    }
}
