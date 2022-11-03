package cn.aulang.oauth.controller;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.BeiAn;
import cn.aulang.oauth.entity.BeiAnEntry;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.entity.MailServer;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.AccountBiz;
import cn.aulang.oauth.manage.BeiAnBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.manage.MailServerBiz;
import cn.aulang.oauth.manage.ThirdServerBiz;
import cn.aulang.oauth.util.PasswordUtils;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

import static cn.aulang.oauth.common.Constants.DEFAULT_KEY;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 15:30
 */
@Controller
public class IndexController {

    private final BeiAnBiz beiAnBiz;
    private final ClientBiz clientBiz;
    private final AccountBiz accountBiz;
    private final ThirdServerBiz serverBiz;
    private final MailServerBiz mailServerBiz;

    @Autowired
    public IndexController(BeiAnBiz beiAnBiz, ClientBiz clientBiz, AccountBiz accountBiz,
                           ThirdServerBiz serverBiz, MailServerBiz mailServerBiz) {
        this.beiAnBiz = beiAnBiz;
        this.clientBiz = clientBiz;
        this.accountBiz = accountBiz;
        this.serverBiz = serverBiz;
        this.mailServerBiz = mailServerBiz;
    }

    @GetMapping({"/", "/index",})
    public String index() {
        return "index";
    }

    @ResponseBody
    @GetMapping(path = "/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> init() {
        Map<String, Object> result = new HashMap<>();
        if (clientBiz.getOne() == null) {
            Client client = new Client();

            client.setName("Aulang");

            client.setLogoUrl("/oauth/images/logo.png");

            client.getScopes().put("basic", "基本信息");
            client.getScopes().put("identity", "身份信息");
            client.getAutoApprovedScopes().add("basic");

            client.getAuthorizationGrants().add("implicit");
            client.getAuthorizationGrants().add("password");
            client.getAuthorizationGrants().add("authorization_code");

            client.getRegisteredRedirectUris().add("https://aulang.cn\\S*");

            client = clientBiz.save(client);

            result.put("client", client);

            MailServer server = mailServerBiz.get();
            server.setPass(new AES(DEFAULT_KEY).encryptHex("xxxxxxxxxx"));
            mailServerBiz.save(server);

            BeiAn beiAn = beiAnBiz.get();

            beiAn.setMiit(BeiAnEntry.of("鄂ICP备18028762号", "http://beian.miit.gov.cn"));
            beiAn.setMps(BeiAnEntry.of("鄂公网安备42011102003833号", "http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=42011102003833"));

            beiAnBiz.save(beiAn);
        }

        if (accountBiz.getOne() == null) {
            Account account = new Account();

            account.setUsername("admin");
            account.setMobile("17620157736");
            account.setEmail("aulang@qq.com");

            account.setPassword(PasswordUtils.digestAndBcrypt("xxxxxxxxxx"));

            account = accountBiz.save(account);

            result.put("account", account);
        }

        if (serverBiz.getOne() == null) {
            ThirdServer server = new ThirdServer();

            server.setName("QQ");
            server.setLogoUrl("/oauth/images/qq.png");

            server.setAuthorizeUrl("https://graph.qq.com/oauth2.0/authorize");
            server.getAuthorizeParams().put("response_type", "code");
            server.getAuthorizeParams().put("client_id", "101869860");
            server.getAuthorizeParams().put("redirect_uri", "https://aulang.cn/oauth/third_login");
            server.getAuthorizeParams().put("scope", "get_user_info");

            server.setAccessTokenUrl("https://graph.qq.com/oauth2.0/token");
            server.setAccessTokenMethod("get");
            server.setAccessTokenType("text");
            server.getAccessTokenParams().put("grant_type", "authorization_code");
            server.getAccessTokenParams().put("client_id", "101869860");
            server.getAccessTokenParams().put("client_secret", "xxxxxxxxxx");
            server.getAccessTokenParams().put("redirect_uri", "https://aulang.cn/oauth/third_login");

            server.setProfileUrl("https://graph.qq.com/oauth2.0/me");
            server.setProfileMethod("get");

            server = serverBiz.save(server);

            result.put("server", server);
        }

        return result;
    }
}
