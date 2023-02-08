package cn.aulang.oauth.captcha;

import com.pig4cloud.captcha.ChineseCaptcha;
import com.pig4cloud.captcha.SpecCaptcha;
import com.pig4cloud.captcha.base.Captcha;

/**
 * @author wulang
 */
public class CaptchaFactory {

    private final CaptchaProperties properties;

    public CaptchaFactory(CaptchaProperties properties) {
        this.properties = properties;
    }

    public Captcha create() {
        switch (properties.getStyle().toLowerCase()) {
            case "spec":
                return new SpecCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            case "chinese":
                return new ChineseCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            default:
                return new MathCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
        }
    }
}
