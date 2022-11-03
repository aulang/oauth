package cn.aulang.oauth.captcha;

import com.pig4cloud.captcha.ChineseCaptcha;
import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.base.Captcha;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:56
 */
public class CaptchaFactory {

    private final CaptchaProperties properties;

    public CaptchaFactory(CaptchaProperties properties) {
        this.properties = properties;
    }

    public Captcha create() {
        return switch (properties.getStyle().toLowerCase()) {
            case "spec" -> new SpecCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            case "chinese" -> new ChineseCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            default -> new MathCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
        };
    }
}
