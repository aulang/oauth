package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.ApprovedScopeRepository;
import lombok.extern.slf4j.Slf4j;
import cn.aulang.oauth.entity.ApprovedScope;
import cn.aulang.oauth.entity.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/6 13:35
 */
@Slf4j
@Service
public class ApprovedScopeBiz {
    @Autowired
    private ApprovedScopeRepository dao;

    public ApprovedScope findByAccountIdAndClientId(String accountId, String clientId) {
        return dao.findByAccountIdAndClientId(accountId, clientId);
    }

    public ApprovedScope save(ApprovedScope entity) {
        return dao.save(entity);
    }

    public ApprovedScope save(Client client, String accountId, String[] scopes) {
        ApprovedScope approvedScope = new ApprovedScope();

        approvedScope.setClientId(client.getId());
        approvedScope.setAccountId(accountId);

        approvedScope.setApproved(new HashSet<>(Arrays.asList(scopes)));

        LocalDateTime now = LocalDateTime.now();
        approvedScope.setExpiresAt(now.plusSeconds(client.getApprovalValiditySeconds()));

        return save(approvedScope);
    }
}
