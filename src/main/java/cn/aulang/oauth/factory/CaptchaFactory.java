package cn.aulang.oauth.factory;

import cn.aulang.oauth.property.CaptchaProperties;
import com.wf.captcha.ChineseCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import cn.aulang.oauth.captcha.MathCaptcha;

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
