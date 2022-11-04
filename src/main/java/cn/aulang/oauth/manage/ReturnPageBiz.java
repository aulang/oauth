package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.ApprovedScope;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletResponse;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-4 21:23
 */
@Service
public class ReturnPageBiz {

    private final ClientBiz clientBiz;
    private final ThirdServerBiz serverBiz;
    private final AuthRequestBiz requestBiz;
    private final ApprovedScopeBiz approvedScopeBiz;

    @Autowired
    public ReturnPageBiz(ClientBiz clientBiz, ThirdServerBiz serverBiz, AuthRequestBiz requestBiz, ApprovedScopeBiz approvedScopeBiz) {
        this.clientBiz = clientBiz;
        this.serverBiz = serverBiz;
        this.requestBiz = requestBiz;
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
        return requestBiz.redirect(request, response, model);
    }

    public String rejectToken(AuthRequest request) {
        String redirectUri = request.getRedirectUri();

        StringBuilder url = new StringBuilder(Constants.REDIRECT);
        url.append(redirectUri);

        if (url.lastIndexOf(Constants.QUESTION) == -1) {
            url.append(Constants.QUESTION);
        } else {
            url.append(Constants.AND);
        }

        url.append("error=reject");

        if (request.getState() != null) {
            url.append(Constants.AND)
                    .append(OAuthConstants.STATE)
                    .append(Constants.EQUAL)
                    .append(request.getState());
        }

        return url.toString();
    }
}
