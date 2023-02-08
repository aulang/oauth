package cn.aulang.oauth.captcha;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wulang
 */
@Data
@ConfigurationProperties("captcha")
public class CaptchaProperties {

    private int len;
    private int width;
    private int height;
    private String style;
}
