package net.aulang.oauth.manage;

import cn.hutool.core.collection.CollectionUtil;
import net.aulang.oauth.common.Constants;
import net.aulang.oauth.common.OAuthConstants;
import net.aulang.oauth.entity.AccountToken;
import net.aulang.oauth.entity.ApprovedScope;
import net.aulang.oauth.entity.AuthRequest;
import net.aulang.oauth.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:23
 */
@Service
public class ReturnPageBiz {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private ThirdServerBiz serverBiz;
    @Autowired
    private AuthRequestBiz requestBiz;
    @Autowired
    private ApprovedScopeBiz approvedScopeBiz;

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
            /**
             * 自动授权
             */
            if (client.getAutoApprovedScopes() != null
                    && client.getAutoApprovedScopes().containsAll(requestScopes)) {
                return grantToken(request, response, model);
            }
            /**
             * 用户已授权
             */
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
            /**
             * 没有请求权限
             */
            return grantToken(request, response, model);
        }

        /**
         * 需要用户授权
         */
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
        return requestBiz.redirect(request, response, model);
    }

    /**
     * 授权单点登录Token
     */
    public String grantSsoToken(String redirectUrl, String state, AccountToken accountToken) {
        /**
         * access_token=ACCESS_TOKEN&expires_in=EXPIRES_IN&state=STATE
         */
        StringBuilder url = new StringBuilder("redirect:");
        url.append(redirectUrl);


        if (url.lastIndexOf(Constants.QUESTION) == -1) {
            url.append(Constants.QUESTION);
        } else {
            url.append(Constants.AND);
        }

        String accessToken = accountToken.getAccessToken();

        Duration duration = Duration.between(LocalDateTime.now(), accountToken.getAccessTokenExpiresAt());

        long expiresIn = duration.getSeconds();
        url.append(OAuthConstants.ACCESS_TOKEN)
                .append(Constants.EQUAL)
                .append(accessToken)
                .append(Constants.AND)
                .append(OAuthConstants.EXPIRES_IN)
                .append(Constants.EQUAL)
                .append(expiresIn);
        if (state != null) {
            url.append(Constants.AND)
                    .append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(state);
        }
        return url.toString();
    }

    public String rejectToken(AuthRequest request) {
        String redirectUrl = request.getRedirectUrl();

        StringBuilder url = new StringBuilder("redirect:");
        url.append(redirectUrl);

        if (url.lastIndexOf(Constants.QUESTION) == -1) {
            url.append(Constants.QUESTION);
        } else {
            url.append(Constants.AND);
        }

        url.append("error=reject");
        return url.toString();
    }
}
