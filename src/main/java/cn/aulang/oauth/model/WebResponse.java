package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web相应
 *
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebResponse<T> {

    private int code;
    private String msg;
    private T data;

    public WebResponse(T data) {
        this.code = 0;
        this.data = data;
    }

    public WebResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static WebResponse<?> success(String... msg) {
        String message = msg.length > 0 ? msg[0] : null;
        return new WebResponse<>(0, message);
    }

    public static WebResponse<?> fail(String... msg) {
        String message = msg.length > 0 ? msg[0] : null;
        return new WebResponse<>(-1, message);
    }

    public static WebResponse<?> of(int code, String msg) {
        return new WebResponse<>(code, msg);
    }

    public static <T> WebResponse<T> of(int code, T data) {
        return new WebResponse<>(code, null, data);
    }
}
