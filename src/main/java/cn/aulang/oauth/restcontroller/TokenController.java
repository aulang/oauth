package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:43
 */
@RestController
public class TokenController {

    private final ClientBiz clientBiz;
    private final AccountTokenBiz tokenBiz;
    private final AuthRequestBiz requestBiz;

    @Autowired
    public TokenController(ClientBiz clientBiz, AccountTokenBiz tokenBiz, AuthRequestBiz requestBiz) {
        this.clientBiz = clientBiz;
        this.tokenBiz = tokenBiz;
        this.requestBiz = requestBiz;
    }

    @PostMapping(path = "/api/token", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> captcha(@RequestParam(name = "authorize_id") String authorizeId,
                                          @RequestParam(name = "mobile") String mobile,
                                          @RequestParam(name = "captcha") String captcha) {
        AuthRequest request = requestBiz.findOne(authorizeId);
        if (request == null || request.getAccountId() == null) {
            return ResponseEntity.badRequest().body(Constants.error("验证码已失效"));
        }

        if (request.getMobile() == null || !request.getMobile().equals(mobile)) {
            return ResponseEntity.badRequest().body(Constants.error("手机号码不匹配"));
        }

        if (request.getCaptcha() == null || !request.getCaptcha().equals(captcha)) {
            return ResponseEntity.badRequest().body(Constants.error("验证码错误"));
        }

        request.setAuthenticated(true);
        requestBiz.save(request);

        String clientId = request.getClientId();
        Client client = clientBiz.findOne(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error("无效的客户端"));
        }

        AccountToken accountToken = tokenBiz.create(
                request.getClientId(),
                client.getAutoApprovedScopes(),
                request.getRedirectUri(),
                request.getAccountId()
        );

        return ResponseEntity.ok(
                AccessToken.create(
                        accountToken.getAccessToken(),
                        accountToken.getRefreshToken(),
                        client.getAccessTokenValiditySeconds()
                )
        );
    }
}
