package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.ApprovedScope;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:23
 */
@Slf4j
@Service
public class ReturnPageBiz {

    private final ClientBiz clientBiz;
    private final AuthCodeBiz codeBiz;
    private final ThirdServerBiz serverBiz;
    private final AccountTokenBiz tokenBiz;
    private final ApprovedScopeBiz approvedScopeBiz;

    @Autowired
    public ReturnPageBiz(ClientBiz clientBiz, AuthCodeBiz codeBiz, ThirdServerBiz serverBiz,
                         AccountTokenBiz tokenBiz, ApprovedScopeBiz approvedScopeBiz) {
        this.clientBiz = clientBiz;
        this.codeBiz = codeBiz;
        this.serverBiz = serverBiz;
        this.tokenBiz = tokenBiz;
        this.approvedScopeBiz = approvedScopeBiz;
    }

    public String loginPage(AuthRequest request, Client client, Model model) {
        String authorizeId = request.getId();

        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("captcha", request.getTriedTimes() > 2);

        if (client == null) {
            client = clientBiz.findOne(request.getClientId());
        }

        model.addAttribute("clientName", client.getName());
        model.addAttribute("servers", serverBiz.getAllServers());

        return "login";
    }

    public String approvalPage(AuthRequest request, HttpServletResponse response, Model model) {
        String authorizeId = request.getId();

        if (!request.isAuthenticated()) {
            return loginPage(request, null, model);
        }

        Client client = clientBiz.findOne(request.getClientId());
        if (client == null) {
            return Constants.errorPage(model, "无效的客户端");
        }

        Set<String> requestScopes = request.getScopes();
        if (CollectionUtil.isNotEmpty(requestScopes)) {
            // 自动授权
            if (client.getAutoApprovedScopes() != null
                    && client.getAutoApprovedScopes().containsAll(requestScopes)) {
                return grantToken(request, response, model);
            }
            // 用户已授权
            ApprovedScope approvedScope = approvedScopeBiz.findByAccountIdAndClientId(
                    request.getAccountId(),
                    client.getId()
            );
            if (approvedScope != null
                    && approvedScope.getApproved() != null
                    && approvedScope.getApproved().containsAll(requestScopes)) {
                return grantToken(request, response, model);
            }
        } else {
            // 没有请求权限
            return grantToken(request, response, model);
        }

        // 需要用户授权
        model.addAttribute("scopes", requestScopes);
        model.addAttribute("authorizeId", authorizeId);
        model.addAttribute("clientName", client.getName());
        model.addAttribute("logoUrl", client.getLogoUrl());
        model.addAttribute("scopeNames", client.getScopes());

        return "approval_scope";
    }

    public String grantToken(AuthRequest request, HttpServletResponse response, Model model) {
        if (!request.isAuthenticated()) {
            return loginPage(request, null, model);
        }
        return redirect(request, response, model);
    }

    public String rejectToken(AuthRequest request) {
        // URL锚点#处理
        List<String> strings = StrUtil.splitTrim(request.getRedirectUri(), Constants.HASH, 2);
        StringBuilder redirectUri = buildRedirectUri(strings.get(0));

        redirectUri.append("error=reject");

        if (request.getState() != null) {
            redirectUri.append(Constants.AND)
                    .append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(request.getState());
        }

        if (strings.size() > 1) {
            redirectUri.append(Constants.HASH).append(strings.get(1));
        }

        return redirectUri.toString();
    }

    public String redirect(AuthRequest request, HttpServletResponse response, Model model) {
        // URL锚点#处理
        List<String> strings = StrUtil.splitTrim(request.getRedirectUri(), Constants.HASH, 2);
        StringBuilder redirectUri = buildRedirectUri(strings.get(0));

        switch (request.getAuthorizationGrant()) {
            case OAuthConstants.AuthorizationGrant.IMPLICIT -> {
                // 简化模式: access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN
                Client client = clientBiz.findOne(request.getClientId());
                String accessToken = tokenBiz.create(request.getClientId(), request.getScopes(),
                        request.getRedirectUri(), request.getAccountId()).getAccessToken();
                long expires_in = client.getAccessTokenValiditySeconds();

                redirectUri.append(OAuthConstants.ACCESS_TOKEN).append(Constants.EQUAL).append(accessToken)
                        .append(Constants.AND)
                        .append(OAuthConstants.EXPIRES_IN).append(Constants.EQUAL).append(expires_in);

                // 设置单点登录Cookie
                Constants.setSsoCookie(response, accessToken);
            }
            case OAuthConstants.AuthorizationGrant.AUTHORIZATION_CODE -> {
                // 授权码模式：code=CODE
                String code = codeBiz.create(request.getClientId(), request.getScopes(), request.getRedirectUri(),
                        request.getCodeChallenge(), request.getAccountId()).getId();

                redirectUri.append(OAuthConstants.CODE).append(Constants.EQUAL).append(code);
            }
            default -> {
                log.error("错误的授权方式，登录认证ID：{}，授权方式：{}", request.getId(), request.getAuthorizationGrant());
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
