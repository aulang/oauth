package cn.aulang.oauth.thirdserver.core;

import lombok.Data;

import java.util.Map;

/**
 * 第三方令牌
 * @author wulang
 */
@Data
public class AccessToken {

    private String accessToken;
    private String refreshToken;
    private String expiresIn;

    private Map<String, String> attributes;
}
