package net.aulang.oauth.controller;

import net.aulang.oauth.entity.Account;
import net.aulang.oauth.entity.BeiAn;
import net.aulang.oauth.entity.BeiAnEntry;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.manage.AccountBiz;
import net.aulang.oauth.manage.BeiAnBiz;
import net.aulang.oauth.manage.ClientBiz;
import net.aulang.oauth.manage.ThirdServerBiz;
import net.aulang.oauth.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 15:30
 */
@Controller
public class IndexController {
    @Autowired
    private BeiAnBiz beiAnBiz;
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private ThirdServerBiz serverBiz;

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

            client.getRegisteredRedirectUrl().add("https://aulang.cn\\S*");

            client = clientBiz.save(client);

            result.put("client", client);

            BeiAn beiAn = beiAnBiz.get();

            beiAn.setMiit(BeiAnEntry.of("鄂ICP备18028762号", "http://beian.miit.gov.cn"));
            beiAn.setMps(BeiAnEntry.of("鄂公网安备42011102003833号", "http://www.beian.gov.cn/portal/registerSystemInfo?recordcode=42011102003833"));

            beiAnBiz.save(beiAn);
        }

        if (accountBiz.getOne() == null) {
            Account account = new Account();

            account.setUsername("admin");
            account.setMobile("xxxxxxxxxxxxxxxxx");
            account.setEmail("aulang@qq.com");

            account.setPassword(PasswordUtil.digest("xxxxxxxxxxxxxxxxx"));

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
            server.getAccessTokenParams().put("client_secret", "xxxxxxxxxxxxxxxxx");
            server.getAccessTokenParams().put("redirect_uri", "https://aulang.cn/oauth/third_login");

            server.setProfileUrl("https://graph.qq.com/oauth2.0/me");
            server.setProfileMethod("get");

            server = serverBiz.save(server);

            result.put("server", server);
        }

        return result;
    }
}
