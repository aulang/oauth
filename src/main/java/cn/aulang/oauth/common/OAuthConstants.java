package cn.aulang.oauth.common;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 13:45
 */
public interface OAuthConstants {
    String CODE = "code";
    String STATE = "state";
    String TOKEN = "token";

    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    /**
     * 种授权方式
     */
    interface AuthorizationGrant {
        String AUTHORIZATION_CODE = "authorization_code";
        String IMPLICIT = "implicit";
        String PASSWORD = "password";
        String CLIENT_CREDENTIALS = "client_credentials";
        String REFRESH_TOKEN = "refresh_token";

        static String typeOf(String responseType) {
            switch (responseType.toLowerCase()) {
                case CODE:
                    return AUTHORIZATION_CODE;
                case TOKEN:
                    return IMPLICIT;
                case PASSWORD:
                    return PASSWORD;
                case CLIENT_CREDENTIALS:
                    return CLIENT_CREDENTIALS;
                default:
                    return null;
            }
        }
    }
}
