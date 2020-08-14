package net.aulang.oauth.restcontroller;

import net.aulang.oauth.common.Constants;
import net.aulang.oauth.entity.AccountToken;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.model.AccessToken;
import net.aulang.oauth.manage.AccountTokenBiz;
import net.aulang.oauth.manage.AuthRequestBiz;
import net.aulang.oauth.manage.ClientBiz;
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
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountTokenBiz tokenBiz;
    @Autowired
    private AuthRequestBiz requestBiz;

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
            ResponseEntity.badRequest().body(Constants.error("无效的客户端"));
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
