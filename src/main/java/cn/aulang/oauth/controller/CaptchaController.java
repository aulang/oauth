package cn.aulang.oauth.controller;

import cn.aulang.framework.exception.CommonError;
import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.factory.CaptchaFactory;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.bo.CaptchaSendResult;
import cn.aulang.oauth.model.request.SendCaptchaRequest;
import cn.aulang.oauth.model.response.SendCaptchaVO;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.RandomUtil;
import com.wf.captcha.base.Captcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import javax.validation.Valid;

import static cn.hutool.core.text.CharSequenceUtil.isNotBlank;

/**
 * 验证码控制器
 *
 * @author Aulang
 * @date 2021-06-17 22:59
 */
@RestController
@RequestMapping("/api/captcha")
public class CaptchaController {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private CaptchaFactory factory;
    @Autowired
    private AuthRequestBiz authRequestBiz;

    /**
     * 发送验证码
     */
    @PostMapping("/send")
    public Response<?> send(@Valid @RequestBody SendCaptchaRequest request) {
        String authId = request.getAuthId();
        String clientId = request.getClientId();

        if (CharSequenceUtil.isAllBlank(authId, clientId)) {
            throw CommonError.BAD_REQUEST.exception();
        }

        AuthRequest authRequest;
        if (isNotBlank(authId)) {
            // Web，先创建认证请求，再发生验证码
            // 登录请求是否存在
            authRequest = authRequestBiz.findOne(authId);
            if (authRequest == null) {
                throw OAuthError.AUTH_REQUEST_NOT_FOUND.exception();
            }

        } else {
            // APP，直接使用客户端发送验证码
            // 客户端是否存在
            Client client = clientBiz.findOne(clientId);
            if (client == null) {
                throw OAuthError.CLIENT_NOT_FOUND.exception();
            }
            authRequest = new AuthRequest();
            authRequest.setResponseType(Constants.MOBILE);
            authRequest.setRedirectUri(Constants.MOBILE);
        }

        String captcha = RandomUtil.randomNumbers(6);
        CaptchaSendResult result = accountBiz.sendCaptcha(request.getMobile(), captcha);

        authRequest.setCaptcha(captcha);
        authRequest.setMobile(request.getMobile());
        authRequest.setAccountId(result.getAccountId());
        authRequest = authRequestBiz.save(authRequest);

        return ResponseFactory.success(SendCaptchaVO.of(authRequest.getId(), result.getTarget()));
    }

    /**
     * 绘制验证码
     */
    @GetMapping(path = "/{authId}", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<StreamingResponseBody> draw(@PathVariable("authId") String authId) {
        AuthRequest request = authRequestBiz.findOne(authId);
        if (request != null) {
            Captcha captcha = factory.create();

            String code = captcha.text();
            request.setCaptcha(code);
            authRequestBiz.save(request);

            return ResponseEntity.ok(captcha::out);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{authId}/{code}")
    public Response<?> send(@PathVariable("authId") String authId, @PathVariable("code") String code) {
        AuthRequest request = authRequestBiz.findOne(authId);

        if (request != null && code.equalsIgnoreCase(request.getCaptcha())) {
            return ResponseFactory.success();
        }

        return ResponseFactory.build(OAuthError.CAPTCHA_ERROR.exception());
    }
}
