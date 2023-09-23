package cn.aulang.oauth.common;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import cn.aulang.common.web.WebResponse;
import org.springframework.ui.Model;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author wulang
 */
public interface Constants {

    String SSO_COOKIE_NAME = "SSO";

    String SEPARATOR = ",";

    String QUESTION = "?";
    String EQUAL = "=";
    String AND = "&";
    String HASH = "#";

    String JSON = "json";

    String GET = "get";
    String POST = "post";
    String HEADER = "header";
    String BEARER = "Bearer";

    String NA = "N/A";

    /**
     * 0开始，3次
     */
    int NEED_CAPTCHA_TIMES = 2;
    /**
     * 0开始，6次
     */
    int MAX_PASSWORD_ERROR_TIMES = 5;
    int DEFAULT_MAX_CLOCK_SKEW_SECONDS = 60;

    String REDIRECT = "redirect:";
    String BIND_STATE_AUTHORIZE_ID = "bind_third_account";

    byte[] DEFAULT_KEY = "QGDCilNe3S3Nn8OFqRAhKoS8DRo21jVk".getBytes();

    JsonMapper JSON_MAPPER = JsonMapper.builder()
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .serializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .visibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            .build();

    static WebResponse<?> error(int code, String msg) {
        return new WebResponse<>(code, msg);
    }

    static String errorPage(Model model, String msg) {
        model.addAttribute("error", msg);
        return "error";
    }

    static String errorPage(String loginPage, Model model, String msg) {
        model.addAttribute("error", msg);
        return LoginPage.pageOf(loginPage, "error");
    }

    static void setSsoCookie(HttpServletResponse response, String authorizeId) {
        if (response == null || response.isCommitted()) {
            return;
        }

        Cookie cookie = new Cookie(SSO_COOKIE_NAME, authorizeId);
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

    TypeReference<Map<String, String>> MAP_STRING_REFERENCE = new TypeReference<>() {
    };

    static Map<String, String> toMap(String json) throws Exception {
        return JSON_MAPPER.readValue(json, MAP_STRING_REFERENCE);
    }
}