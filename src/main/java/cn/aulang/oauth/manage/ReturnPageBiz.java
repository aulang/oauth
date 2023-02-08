package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AccountToken;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.List;

/**
 * @author wulang
 */
@Slf4j
@Service
public class ReturnPageBiz {

    private final ClientBiz clientBiz;
    private final AuthCodeBiz codeBiz;
    private final ThirdServerBiz serverBiz;
    private final AccountTokenBiz tokenBiz;

    @Autowired
    public ReturnPageBiz(ClientBiz clientBiz, AuthCodeBiz codeBiz, ThirdServerBiz serverBiz, AccountTokenBiz tokenBiz) {
        this.clientBiz = clientBiz;
        this.codeBiz = codeBiz;
        this.serverBiz = serverBiz;
        this.tokenBiz = tokenBiz;
    }

    public String changePwdPage(AuthRequest request, Model model) {
        model.addAttribute("authorizeId", request.getId());
        model.addAttribute("error", request.getChpwdReason());
        return "change_passwd";
    }

    public String forgetPwdPage(AuthRequest request, Model model) {
        model.addAttribute("authorizeId", request.getId());
        return "forget_passwd";
    }

    public String loginPage(AuthRequest request, Client client, Model model) {
        String authorizeId = request.getId();

        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("captcha", request.getTriedTimes() > 2);

        if (client == null) {
            client = clientBiz.get(request.getClientId());
        }

        model.addAttribute("clientName", client.getName());
        model.addAttribute("servers", serverBiz.getAllServers());

        return "login";
    }

    public String redirect(AuthRequest request, Model model) {
        // URL锚点#处理
        List<String> strings = StrUtil.split(request.getRedirectUri(), Constants.HASH, 2);
        StringBuilder redirectUri = buildRedirectUri(strings.get(0));

        switch (request.getAuthGrant()) {
            case OAuthConstants.AuthorizationGrant.IMPLICIT -> {
                // 简化模式: access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN
                Client client = clientBiz.get(request.getClientId());
                AccountToken accountToken = tokenBiz.create(request.getClientId(), request.getRedirectUri(), request.getAccountId());
                long expiresIn = client.getAccessTokenExpiresIn();

                redirectUri.append(OAuthConstants.ACCESS_TOKEN).append(Constants.EQUAL).append(accountToken.getAccessToken())
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
                return Constants.errorPage(model, "错误的授权方式");
            }
        }

        // 如果有state要添加state
        // &state=STATE
        String state = request.getState();
        if (StrUtil.isNotBlank(state)) {
            redirectUri.append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(state)
                    .append(Constants.AND);
        }

        // URL锚点#处理
        if (strings.size() > 1) {
            redirectUri.append(Constants.HASH).append(strings.get(1));
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
