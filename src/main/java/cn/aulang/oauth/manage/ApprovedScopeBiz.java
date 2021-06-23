package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.ApprovedScope;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.ApprovedScopeRepository;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/6 13:35
 */
@Slf4j
@Service
public class ApprovedScopeBiz {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private ApprovedScopeRepository dao;

    public ApprovedScope findByAccountIdAndClientId(String accountId, String clientId) {
        return dao.findByAccountIdAndClientId(accountId, clientId);
    }

    public ApprovedScope save(ApprovedScope entity) {
        return dao.save(entity);
    }

    public ApprovedScope create(Client client, String accountId, Set<String> scopes) {
        ApprovedScope approvedScope = new ApprovedScope();

        approvedScope.setClientId(client.getId());
        approvedScope.setAccountId(accountId);

        approvedScope.setApproved(scopes);

        LocalDateTime now = LocalDateTime.now();
        approvedScope.setExpiresAt(now.plusSeconds(client.getApprovalValiditySeconds()));

        approvedScope.setLastUpdatedAt(now);

        return save(approvedScope);
    }

    public void hasApproved(AuthRequest authRequest) throws BaseException {
        Client client = clientBiz.getClient(authRequest.getClientId());

        Set<String> requestScopes = authRequest.getScopes();
        if (CollectionUtil.isEmpty(requestScopes)) {
            return;
        }

        // 没有申请额外权限，自动授权
        if (client.getAutoApprovedScopes() != null
                && client.getAutoApprovedScopes().containsAll(requestScopes)) {
            return;
        }
        // 用户已经授权过，无需再授权
        ApprovedScope approvedScope = findByAccountIdAndClientId(
                authRequest.getAccountId(),
                client.getId()
        );
        if (approvedScope != null
                && approvedScope.getApproved() != null
                && approvedScope.getApproved().containsAll(requestScopes)) {
            return;
        }

        throw OAuthError.NEED_APPROVAL.exception();
    }

    public void approved(AuthRequest authRequest) throws BaseException {
        ApprovedScope approved = findByAccountIdAndClientId(authRequest.getAccountId(), authRequest.getClientId());
        if (approved != null) {
            approved.setApproved(authRequest.getScopes());

            LocalDateTime now = LocalDateTime.now();
            approved.setExpiresAt(now.plusSeconds(approved.getExpiresIn()));
            approved.setLastUpdatedAt(now);

            save(approved);
        } else {
            Client client = clientBiz.getClient(authRequest.getClientId());
            create(client, authRequest.getAccountId(), authRequest.getScopes());
        }
    }
}
