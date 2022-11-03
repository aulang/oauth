package cn.aulang.oauth.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:25
 */
@Data
@ConfigurationProperties("captcha")
public class CaptchaProperties {

    private int len;
    private int width;
    private int height;
    private String style;
}
