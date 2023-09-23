package cn.aulang.oauth.restcontroller;

import cn.hutool.core.util.RandomUtil;
import com.pig4cloud.captcha.base.Captcha;
import cn.aulang.oauth.captcha.CaptchaFactory;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.model.SendCaptchaResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 验证码服务
 *
 * @author wulang
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CaptchaController {

    private final AccountBiz accountBiz;
    private final CaptchaFactory factory;
    private final AuthRequestBiz requestBiz;

    @Autowired
    public CaptchaController(AccountBiz accountBiz, CaptchaFactory factory, AuthRequestBiz requestBiz) {
        this.accountBiz = accountBiz;
        this.factory = factory;
        this.requestBiz = requestBiz;
    }

    @GetMapping(path = "/captcha/{authorizeId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<StreamingResponseBody> captcha(@PathVariable String authorizeId) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request != null) {
            Captcha captcha = factory.create();

            String code = captcha.text();
            request.setCaptcha(code);
            requestBiz.save(request);

            return ResponseEntity.ok(captcha::out);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/captcha/{authorizeId}/{code}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> captcha(@PathVariable String authorizeId, @PathVariable String code) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request != null && code.equals(request.getCaptcha())) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping(path = "/captcha", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> captcha(@RequestParam(name = "authorize_id", required = false) String authorizeId,
                                     @RequestParam(name = "client_id", required = false) String clientId,
                                     @RequestParam(name = "mobile") String mobile) {
        AuthRequest request;
        if (StringUtils.isNotBlank(authorizeId)) {
            // Web端
            request = requestBiz.get(authorizeId);
            if (request == null) {
                return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "登录请求不存在或已失效"));
            }
        } else if (StringUtils.isNotBlank(clientId)) {
            // 移动端
            request = new AuthRequest();
            request.setClientId(clientId);
            // 移动端验证码登录没有redirectUri默认填充captcha
            request.setRedirectUri("captcha");
        } else {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "参数不合法"));
        }

        String captcha = RandomUtil.randomNumbers(6);

        String accountId;
        SendCaptchaResult result;
        try {
            result = accountBiz.sendCaptcha(mobile, captcha);
            if (result == null) {
                return ResponseEntity.badRequest().body("账号未注册或者没有绑定手机、邮箱登录");
            }
            accountId = result.getAccountId();
        } catch (Exception e) {
            log.error("发送验证码失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Constants.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "发送验证码失败"));
        }

        request.setMobile(mobile);
        request.setCaptcha(captcha);
        request.setAccountId(accountId);
        request = requestBiz.save(request);

        // 不能返回给前端
        result.setAccountId(null);
        result.setAuthorizeId(request.getId());

        return ResponseEntity.ok(result);
    }
}
