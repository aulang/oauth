package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthTokenBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wulang
 */
@RestController
@RequestMapping("/api")
public class TokenController {

    private final ClientBiz clientBiz;
    private final AuthTokenBiz tokenBiz;
    private final AuthRequestBiz requestBiz;

    @Autowired
    public TokenController(ClientBiz clientBiz, AuthTokenBiz tokenBiz, AuthRequestBiz requestBiz) {
        this.clientBiz = clientBiz;
        this.tokenBiz = tokenBiz;
        this.requestBiz = requestBiz;
    }

    @PostMapping(path = "/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> captcha(@RequestParam(name = "authorize_id") String authorizeId,
                                     @RequestParam(name = "mobile") String mobile,
                                     @RequestParam(name = "captcha") String captcha) {
        AuthRequest request = requestBiz.get(authorizeId);
        if (request == null || request.getAccountId() == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "验证码已失效"));
        }

        if (request.getMobile() == null || !request.getMobile().equals(mobile)) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "手机号码不匹配"));
        }

        if (request.getCaptcha() == null || !request.getCaptcha().equals(captcha)) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "验证码错误"));
        }

        request.setAuthenticated(true);
        requestBiz.save(request);

        String clientId = request.getClientId();
        Client client = clientBiz.get(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的客户端"));
        }

        AuthToken authToken = tokenBiz.create(
                request.getClientId(),
                request.getRedirectUri(),
                request.getAccountId()
        );

        return ResponseEntity.ok(
                AccessToken.create(
                        authToken.getAccessToken(),
                        authToken.getRefreshToken(),
                        client.getAccessTokenExpiresIn()
                )
        );
    }
}
