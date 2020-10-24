package cn.aulang.oauth.common;

import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 13:41
 */
public interface Constants {
    String SSO_COOKIE_NAME = "SSO";

    String QUESTION = "?";
    String EQUAL = "=";
    String AND = "&";

    String JSON = "json";

    String GET = "get";
    String POST = "post";
    String HEADER = "header";
    String BEARER = "Bearer";

    int NEED_CAPTCHA_TIMES = 2;
    int MAX_PASSWORD_ERROR_TIMES = 6;

    String REDIRECT = "redirect:";
    String BIND_STATE_AUTHORIZE_ID = "bind_third_account";
    byte[] DEFAULT_KEY = "QGDCilNe3S3Nn8OFqRAhKoS8DRo21jVk".getBytes();


    static Map<String, String> error(String msg) {
        Map<String, String> error = new HashMap<>();
        error.put("error", msg);
        return error;
    }

    static String errorPage(Model model, String msg) {
        model.addAttribute("error", msg);
        return "error";
    }

    static Cookie setSsoCookie(String value) {
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        return cookie;
    }

    static Cookie removeSsoCookie() {
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }
}