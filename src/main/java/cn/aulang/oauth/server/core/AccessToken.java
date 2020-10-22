package cn.aulang.oauth.server.core;

import lombok.Data;

import java.util.Map;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:09
 * 第三方令牌
 */
@Data
public class AccessToken {
    private String accessToken;
    private String refreshToken;
    private String expiresIn;

    private Map<String, String> attributes;
}
