package cn.aulang.oauth.common;

/**
 * @author wulang
 */
public interface OAuthConstants {

    String CODE = "code";
    String STATE = "state";
    String TOKEN = "token";

    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    int DEFAULT_EXPIRES_MINUTES = 10;

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
            return switch (responseType.toLowerCase()) {
                case CODE -> AUTHORIZATION_CODE;
                case TOKEN -> IMPLICIT;
                case PASSWORD -> PASSWORD;
                case CLIENT_CREDENTIALS -> CLIENT_CREDENTIALS;
                default -> null;
            };
        }
    }
}
