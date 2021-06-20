package cn.aulang.oauth.captcha;

import cn.hutool.script.ScriptUtil;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.awt.*;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 23:38
 */
@Slf4j
public class MathCaptcha extends ArithmeticCaptcha {
    public MathCaptcha() {
    }

    public MathCaptcha(int width, int height) {
        super(width, height, 2);
    }

    public MathCaptcha(int width, int height, Font font) {
        super(width, height, 2, font);
    }

    @Override
    protected char[] alphas() {
        StringBuilder sb = new StringBuilder();

        // 第一个随机整数
        sb.append(num(10));

        // 运算符，不能用除法
        int type = num(1, 4);
        if (type == 1) {
            sb.append("+");
        } else if (type == 2) {
            sb.append("-");
        } else if (type == 3) {
            sb.append("x");
        }
        // 第二个随机整数
        sb.append(num(10));

        // js引擎计算结果
        ScriptEngine engine = ScriptUtil.getScript("graal.js");
        try {
            chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
        } catch (ScriptException e) {
            log.error("数学验证码结果技术失败", e);
        }
        sb.append("=?");
        setArithmeticString(sb.toString());
        return chars.toCharArray();
    }
}
