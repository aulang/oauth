package cn.aulang.oauth.captcha;

import com.googlecode.aviator.AviatorEvaluator;
import com.pig4cloud.captcha.ArithmeticCaptcha;

import java.awt.*;

/**
 * @author wulang
 */
public class MathCaptcha extends ArithmeticCaptcha {

    public MathCaptcha() {
    }

    public MathCaptcha(int width, int height) {
        super(width, height);
    }

    public MathCaptcha(int width, int height, int len) {
        super(width, height, len);
    }

    public MathCaptcha(int width, int height, int len, Font font) {
        super(width, height, len, font);
    }

    @Override
    protected char[] alphas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(num(10));
            if (i < len - 1) {
                int type = num(1, 4);
                if (type == 1) {
                    sb.append("+");
                } else if (type == 2) {
                    sb.append("-");
                } else if (type == 3) {
                    sb.append("x");
                }
            }
        }

        String expression = sb.toString().replaceAll("x", "*");
        chars = String.valueOf(AviatorEvaluator.execute(expression));

        sb.append("=?");
        setArithmeticString(sb.toString());
        return chars.toCharArray();
    }
}
