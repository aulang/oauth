package cn.aulang.oauth.restcontroller;

import cn.aulang.oauth.thirdserver.mini.MiniService;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.ThirdAccountNotExistException;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.AuthTokenBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.model.AccessToken;
import cn.aulang.oauth.model.ThirdUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountLockedException;
import jakarta.validation.Valid;

/**
 * @author wulang
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class WeChatController {

    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final AuthTokenBiz tokenBiz;
    private final MiniService miniService;
    private final ThirdServerBiz serverBiz;
    private final AuthRequestBiz requestBiz;

    @Autowired
    public WeChatController(ClientBiz clientBiz, AuthTokenBiz tokenBiz, ThirdServerBiz serverBiz,
                            MiniService miniService, AccountBiz accountBiz, AuthRequestBiz requestBiz) {
        this.tokenBiz = tokenBiz;
        this.clientBiz = clientBiz;
        this.serverBiz = serverBiz;
        this.accountBiz = accountBiz;
        this.requestBiz = requestBiz;
        this.miniService = miniService;
    }

    @GetMapping("/code2token")
    public ResponseEntity<?> code2Token(@RequestParam(name = "client_id") String clientId,
                                        @RequestParam(name = "server_id") String serverId,
                                        @RequestParam String code) {
        Client client = clientBiz.get(clientId);
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的client_id"));
        }

        ThirdServer thirdServer = serverBiz.get(serverId);
        if (thirdServer == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的server_id"));
        }

        try {
            Account account = miniService.authenticate(thirdServer, code);
            AuthToken authToken = tokenBiz.create(clientId, thirdServer.getName(), account.getId());

            AccessToken accessToken = AccessToken.create(authToken.getAccessToken(),
                    authToken.getRefreshToken(), client.getAccessTokenExpiresIn());

            return ResponseEntity.ok(accessToken);
        } catch (ThirdAccountNotExistException e) {
            ThirdUser thirdUser = new ThirdUser();
            thirdUser.setServerId(e.getServerId());
            thirdUser.setThirdId(e.getThirdId());
            thirdUser.setOpenId(e.getOpenId());
            thirdUser.setUnionId(e.getUnionId());
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(thirdUser);
        } catch (Exception e) {
            log.error(thirdServer.getName() + "账号登录失败", e);
            return ResponseEntity.internalServerError().body(Constants.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "账号登录失败"));
        }
    }

    @PostMapping("/third_bind")
    public ResponseEntity<?> bind(@RequestBody @Valid ThirdUser thirdUser) {
        Client client = clientBiz.get(thirdUser.getClientId());
        if (client == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的client_id"));
        }

        ThirdServer thirdServer = serverBiz.get(thirdUser.getServerId());
        if (thirdServer == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "无效的server_id"));
        }

        if (StringUtils.isNoneBlank(thirdUser.getUsername(), thirdUser.getPassword())) {
            return passwordBind(thirdUser, thirdServer, client);
        }

        if (StringUtils.isNoneBlank(thirdUser.getAuthorizeId(), thirdUser.getMobile(), thirdUser.getCaptcha())) {
            return captchaBind(thirdUser, thirdServer, client);
        }

        return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "请求参数缺失"));
    }

    private ResponseEntity<?> passwordBind(ThirdUser thirdUser, ThirdServer thirdServer, Client client) {
        String accountId;
        try {
            accountId = accountBiz.login(thirdUser.getUsername(), thirdUser.getPassword());
        } catch (AccountLockedException e) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }

        if (accountId == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "账号或密码错误"));
        }

        return token(thirdUser, thirdServer, client, accountId);
    }

    private ResponseEntity<?> captchaBind(ThirdUser thirdUser, ThirdServer thirdServer, Client client) {
        AuthRequest request = requestBiz.get(thirdUser.getAuthorizeId());
        if (request == null) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "验证码不存在"));
        }

        if (request.getMobile() != null && !thirdUser.getMobile().equals(request.getMobile())) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "手机号码不匹配"));
        }

        if (request.getAccountId() == null && !thirdUser.getCaptcha().equals(request.getCaptcha())) {
            return ResponseEntity.badRequest().body(Constants.error(HttpStatus.BAD_REQUEST.value(), "验证码错误"));
        }

        request.setAuthenticated(true);
        request = requestBiz.save(request);

        return token(thirdUser, thirdServer, client, request.getAccountId());
    }

    private ResponseEntity<?> token(ThirdUser thirdUser, ThirdServer thirdServer, Client client, String accountId) {
        AuthToken authToken = tokenBiz.bindToken(thirdServer, client.getId(), thirdUser.getThirdId(),
                thirdUser.getOpenId(), thirdUser.getUnionId(), accountId);

        AccessToken accessToken = AccessToken.create(authToken.getAccessToken(),
                authToken.getRefreshToken(), client.getAccessTokenExpiresIn());

        return ResponseEntity.ok(accessToken);
    }
}
