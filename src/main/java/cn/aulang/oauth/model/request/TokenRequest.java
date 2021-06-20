package cn.aulang.oauth.model.request;

import lombok.Data;

/**
 * 获取令牌请求
 *
 * @author Aulang
 * @date 2021-06-19 23:00
 */
@Data
public class TokenRequest {
    private String clientId;
    private String grantType;
    private String code;
    private String redirectUri;
    private String codeVerifier;
}
