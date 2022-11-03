package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AccountTokenBiz;
import cn.aulang.oauth.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 19:54
 */
@RestController
public class ProfileRestController {

    private final AccountBiz accountBiz;
    private final AccountTokenBiz tokenBiz;

    @Autowired
    public ProfileRestController(AccountBiz accountBiz, AccountTokenBiz tokenBiz) {
        this.accountBiz = accountBiz;
        this.tokenBiz = tokenBiz;
    }

    @GetMapping(path = "/api/profile", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> me(@RequestHeader("Authorization") String authorization) {
        String accessToken = authorization.substring(Constants.BEARER.length()).trim();
        AccountToken accountToken = tokenBiz.findByAccessToken(accessToken);
        if (accountToken != null) {
            String accountId = accountToken.getAccountId();
            Profile user = accountBiz.getUser(accountId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("账号不存在"));
            }
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Constants.error("无效的access_token"));
        }
    }
}
