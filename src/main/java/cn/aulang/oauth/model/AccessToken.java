package cn.aulang.oauth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
    @JsonProperty("expires_in")
    private int expiresIn;

    public static AccessToken create(String accessToken, String refreshToken, int expiresIn) {
        return new AccessToken(accessToken, refreshToken, expiresIn);
    }
}
