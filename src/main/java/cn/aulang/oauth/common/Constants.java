package cn.aulang.oauth.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
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

    JsonMapper JSON_MAPPER = JsonMapper.builder()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL)
            .build();


    static Map<String, String> error(String msg) {
        Map<String, String> error = new HashMap<>();
        error.put("error", msg);
        return error;
    }

    static String errorPage(Model model, String msg) {
        model.addAttribute("error", msg);
        return "error";
    }

    static void setSsoCookie(HttpServletResponse response, String accessToken) {
        if (response == null || response.isCommitted()) {
            return;
        }

        Cookie cookie = new Cookie(SSO_COOKIE_NAME, accessToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");

        response.addCookie(cookie);
    }

    static void removeSsoCookie(HttpServletResponse response) {
        if (response == null || response.isCommitted()) {
            return;
        }

        Cookie cookie = new Cookie(SSO_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}