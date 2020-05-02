package net.aulang.oauth.common;

import cn.hutool.core.codec.Base64;
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

    int MAX_PASSWORD_ERROR_TIMES = 6;
    
    String BIND_STATE_AUTHORIZE_ID = "bind_third_account";


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
        String base64 = Base64.encode(value);
        Cookie cookie = new Cookie(SSO_COOKIE_NAME, base64);
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