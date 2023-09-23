package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtUser {

    public static final String USER_ID = "user_id";
    public static final String USERNAME = "username";
    public static final String NICKNAME = "nickname";
    public static final String CLIENT_ID = "client_id";

    private String userId;
    private String username;
    private String nickname;
    private String clientId;
    private String tokenId;
}
