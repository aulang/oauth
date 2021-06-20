package cn.aulang.oauth.common;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 13:41
 */
public interface Constants {
    String CODE = "code";
    String STATE = "state";
    String MOBILE = "mobile";

    String EXPIRES_IN = "expires_in";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";

    String QUESTION = "?";
    String EQUAL = "=";
    String AND = "&";
    String COMMA = ",";

    String JSON = "json";

    String GET = "get";
    String POST = "post";
    String HEADER = "header";
    String BEARER = "Bearer";

    String REDIRECT = "redirect:";
    String BIND_STATE_AUTHORIZE_ID = "bind_third_account";
    byte[] DEFAULT_KEY = "QGDCilNe3S3Nn8OFqRAhKoS8DRo21jVk".getBytes();
}