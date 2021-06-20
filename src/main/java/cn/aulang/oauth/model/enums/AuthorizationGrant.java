package cn.aulang.oauth.model.enums;

/**
 * 授权方式
 *
 * @author Aulang
 * @date 2021-06-18 22:26
 */
public enum AuthorizationGrant {
    CODE("code", "authorization_code"),
    REFRESH_TOKEN("refresh_token", "refresh_token"),
    CLIENT_CREDENTIALS("client_credentials", "client_credentials");

    private final String responseType;
    private final String grantType;

    AuthorizationGrant(String responseType, String grantType) {
        this.responseType = responseType;
        this.grantType = grantType;
    }

    public String getResponseType() {
        return responseType;
    }

    public String getGrantType() {
        return grantType;
    }
}
