package net.aulang.oauth.factory;

import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.captcha.ICaptcha;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.captcha.ShearCaptcha;
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

    public ICaptcha create() {
        switch (properties.getStyle().toLowerCase()) {
            case "circle":
                return new CircleCaptcha(properties.getWidth(), properties.getHeight(), properties.getCount());
            case "line":
                return new LineCaptcha(properties.getWidth(), properties.getHeight(), properties.getCount(), 150);
            case "shear":
                return new ShearCaptcha(properties.getWidth(), properties.getHeight(), properties.getCount());
            default:
                return new ShearCaptcha(properties.getWidth(), properties.getHeight(), properties.getCount());
        }
    }
}
