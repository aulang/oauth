package net.aulang.oauth.factory;

import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import net.aulang.oauth.captcha.MathCaptcha;
import net.aulang.oauth.property.CaptchaProperties;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:56
 */
public class CaptchaFactory {
    private CaptchaProperties properties;

    public CaptchaFactory(CaptchaProperties properties) {
        this.properties = properties;
    }

    public Captcha create() {
        switch (properties.getStyle().toLowerCase()) {
            case "Spec":
                return new SpecCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            case "Chinese":
                return new ChineseCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
            default:
                return new MathCaptcha(properties.getWidth(), properties.getHeight(), properties.getLen());
        }
    }
}
