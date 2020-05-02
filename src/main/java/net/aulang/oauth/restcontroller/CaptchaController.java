package net.aulang.oauth.restcontroller;

import cn.hutool.captcha.ICaptcha;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.factory.CaptchaFactory;
import net.aulang.oauth.model.CaptchaSendResult;
import net.aulang.oauth.manage.AccountBiz;
import net.aulang.oauth.manage.AuthRequestBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:15
 * 验证码服务
 */
@Slf4j
@RestController
public class CaptchaController {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private CaptchaFactory factory;
    @Autowired
    private AuthRequestBiz requestBiz;

    @GetMapping(path = "/api/captcha/{authorizeId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<StreamingResponseBody> captcha(@PathVariable String authorizeId) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request != null) {
            ICaptcha captcha = factory.create();

            String code = captcha.getCode();
            request.setCaptcha(code);
            requestBiz.save(request);

            return ResponseEntity.ok(outputStream -> captcha.write(outputStream));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/api/captcha/{authorizeId}/{code}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> captcha(@PathVariable String authorizeId, @PathVariable String code) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request != null && code.equals(request.getCaptcha())) {
            return ResponseEntity.ok("true");
        }
        return ResponseEntity.badRequest().body("false");
    }

    @PostMapping(path = "/api/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> captcha(@RequestParam(name = "authorize_id", required = false) String authorizeId,
                                          @RequestParam(name = "client_id", required = false) String clientId,
                                          @RequestParam(name = "mobile") String mobile) {
        AuthRequest request;
        if (StrUtil.isNotBlank(authorizeId)) {
            /**
             * Web端
             */
            request = requestBiz.findOne(authorizeId);
            if (request == null) {
                return ResponseEntity.badRequest().body("登录请求不存在或已失效");
            }
        } else if (StrUtil.isNotBlank(clientId)) {
            /**
             * 移动端
             */
            request = new AuthRequest();
            request.setClientId(clientId);
            /**
             * 移动端验证码登录没有redirectUrl默认填充captcha
             */
            request.setRedirectUrl("captcha");
        } else {
            return ResponseEntity.badRequest().body("参数不合法");
        }

        String captcha = RandomUtil.randomNumbers(6);

        String accountId;
        CaptchaSendResult result;
        try {
            result = accountBiz.sendCaptcha(mobile, captcha);
            if (result == null) {
                return ResponseEntity.badRequest().body("账号未注册或者没有绑定手机、邮箱登录");
            }
            accountId = result.getAccountId();
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("发送验证码失败");
        }

        request.setMobile(mobile);
        request.setCaptcha(captcha);
        request.setAccountId(accountId);
        request = requestBiz.save(request);

        /**
         * 不能返回给前端
         */
        result.setAccountId(null);
        result.setRequestId(request.getId());

        return ResponseEntity.ok(result);
    }
}
