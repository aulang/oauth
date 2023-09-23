package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.LoginPage;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.AuthToken;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.entity.ThirdServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

/**
 * @author wulang
 */
@Slf4j
@Service
public class ReturnPageBiz {

    private final ClientBiz clientBiz;
    private final AuthCodeBiz codeBiz;
    private final AuthTokenBiz tokenBiz;
    private final AuthRequestBiz requestBiz;
    private final ThirdServerBiz thirdServerBiz;
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public ReturnPageBiz(ClientBiz clientBiz, AuthCodeBiz codeBiz, AuthRequestBiz requestBiz,
                         AuthTokenBiz tokenBiz, ThirdServerBiz thirdServerBiz, ThirdAccountBiz thirdAccountBiz) {
        this.codeBiz = codeBiz;
        this.tokenBiz = tokenBiz;
        this.clientBiz = clientBiz;
        this.requestBiz = requestBiz;
        this.thirdServerBiz = thirdServerBiz;
        this.thirdAccountBiz = thirdAccountBiz;
    }

    public String changePwdPage(AuthRequest request, Model model) {
        model.addAttribute("authorizeId", request.getId());
        model.addAttribute("error", request.getChpwdReason());
        return LoginPage.pageOf(request.getLoginPage(), "change_passwd");

    }

    public String forgetPwdPage(AuthRequest request, Model model) {
        model.addAttribute("authorizeId", request.getId());
        return LoginPage.pageOf(request.getLoginPage(), "forget_passwd");
    }

    public String loginPage(AuthRequest request, Client client, Model model) {
        String authorizeId = request.getId();

        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("captcha", request.getTriedTimes() > 2);

        if (client == null) {
            client = clientBiz.get(request.getClientId());
        }

        model.addAttribute("clientName", client.getName());
        model.addAttribute("servers", thirdServerBiz.getAllServers());

        return LoginPage.pageOf(request.getLoginPage(), "login");
    }

    public String bindPage(AuthRequest request, Model model, String serverId, String thirdId, String openId, String unionId) {
        model.addAttribute("captcha", request.getTriedTimes() > 2);

        model.addAttribute("authorizeId", request.getId());
        model.addAttribute("serverId", serverId);
        model.addAttribute("thirdId", thirdId);
        model.addAttribute("openId", openId);
        model.addAttribute("unionId", unionId);
        return LoginPage.pageOf(request.getLoginPage(), "bind_account");
    }

    @Transactional(rollbackFor = Exception.class)
    public String bindRedirect(AuthRequest request, Model model, ThirdServer thirdServer, String thirdId, String openId, String unionId) {
        thirdAccountBiz.bind(thirdServer.getId(), thirdServer.getType(), thirdId, thirdId, openId, unionId, request.getAccountId());

        request.setAccountId(request.getAccountId());
        request.setAuthenticated(true);
        request = requestBiz.save(request);

        return redirect(request, model);
    }

    public String redirect(AuthRequest request, Model model) {
        // URL锚点#处理
        String[] strings = StringUtils.split(request.getRedirectUri(), Constants.HASH, 2);
        StringBuilder redirectUri = buildRedirectUri(strings[0]);

        switch (request.getAuthGrant()) {
            case OAuthConstants.AuthorizationGrant.IMPLICIT -> {
                // 简化模式: access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN
                Client client = clientBiz.get(request.getClientId());
                AuthToken authToken = tokenBiz.create(request.getClientId(), request.getRedirectUri(), request.getAccountId());
                long expiresIn = client.getAccessTokenExpiresIn();

                redirectUri.append(OAuthConstants.ACCESS_TOKEN).append(Constants.EQUAL).append(authToken.getAccessToken())
                        .append(Constants.AND)
                        .append(OAuthConstants.EXPIRES_IN).append(Constants.EQUAL).append(expiresIn);
            }
            case OAuthConstants.AuthorizationGrant.AUTHORIZATION_CODE -> {
                // 授权码模式：code=CODE
                String code = codeBiz.create(request.getClientId(), request.getRedirectUri(),
                        request.getCodeChallenge(), request.getAccountId()).getId();

                redirectUri.append(OAuthConstants.CODE).append(Constants.EQUAL).append(code);
            }
            default -> {
                log.error("错误的授权方式，登录认证ID：{}，授权方式：{}", request.getId(), request.getAuthGrant());
                return Constants.errorPage(request.getLoginPage(), model, "错误的授权方式");
            }
        }

        // 如果有state要添加state
        // &state=STATE
        String state = request.getState();
        if (StringUtils.isNotBlank(state)) {
            redirectUri.append(Constants.AND)
                    .append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(state);
        }

        // URL锚点#处理
        if (strings.length > 1) {
            redirectUri.append(Constants.HASH).append(strings[1]);
        }

        return redirectUri.toString();
    }

    private StringBuilder buildRedirectUri(String url) {
        StringBuilder redirectUri = new StringBuilder(Constants.REDIRECT);
        redirectUri.append(url);

        // 有?不用再添加?直接添加&
        if (redirectUri.lastIndexOf(Constants.QUESTION) == -1) {
            redirectUri.append(Constants.QUESTION);
        } else {
            redirectUri.append(Constants.AND);
        }

        return redirectUri;
    }
}
