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
public class Profile {

    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String NICKNAME = "nickname";
    public static final String CLIENT_ID = "clientId";

    private String id;
    private String username;
    private String nickname;
    private String clientId;
}
