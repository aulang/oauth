package cn.aulang.oauth.captcha;

import cn.hutool.script.ScriptUtil;
import com.wf.captcha.ArithmeticCaptcha;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.awt.*;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 23:38
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
        ScriptEngine engine = ScriptUtil.getScript("graal.js");
        try {
            chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
        } catch (ScriptException e) {
            e.printStackTrace();
        }
        sb.append("=?");
        setArithmeticString(sb.toString());
        return chars.toCharArray();
    }
}
