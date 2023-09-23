package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.ThirdAccountRepository;
import cn.aulang.oauth.thirdserver.core.Profile;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wulang
 */
@Service
public class ThirdAccountBiz {

    private final AccountBiz accountBiz;
    private final ThirdAccountRepository dao;

    @Autowired
    public ThirdAccountBiz(AccountBiz accountBiz, ThirdAccountRepository dao) {
        this.dao = dao;
        this.accountBiz = accountBiz;
    }

    public ThirdAccount getAccount(Profile profile) {
        Map<String, Object> params = new HashMap<>();
        params.put("serverType", profile.getServerType());
        params.put("thirdId", profile.getId());

        ThirdAccount account = dao.getOneByProperties(ThirdAccount.class, params);
        if (account != null) {
            return account;
        }

        if (StringUtils.isBlank(profile.getUnionId())) {
            return null;
        }

        params.remove("thirdId");
        params.put("unionId", profile.getUnionId());

        List<ThirdAccount> accounts = dao.findByProperties(ThirdAccount.class, params);
        if (CollectionUtils.isEmpty(accounts)) {
            return null;
        }

        account = accounts.get(0);
        return bind(account.getId(), profile);
    }

    @Transactional(rollbackFor = Exception.class)
    public Account register(Profile profile) {
        String thirdId = profile.getId();
        String thirdName = profile.getUsername();
        String serverType = profile.getServerType();
        String username = serverType + "-" + thirdId;

        Account account = new Account();
        account.setUsername(username);
        account.setNickname(thirdName);
        account = accountBiz.registerThirdAccount(account);

        bind(account.getId(), profile);

        return account;
    }

    public ThirdAccount bind(String accountId, Profile profile) {
        return bind(profile.getServerId(), profile.getServerType(), profile.getId(),
                profile.getUsername(), profile.getOpenId(), profile.getUnionId(), accountId);
    }

    public ThirdAccount bind(String serverId, String serverType, String thirdId,
                             String thirdName, String openId, String unionId, String accountId) {
        ThirdAccount entity = new ThirdAccount();

        entity.setAccountId(accountId);
        entity.setServerId(serverId);
        entity.setServerType(serverType);
        entity.setThirdId(thirdId);
        entity.setThirdName(thirdName);

        entity.setOpenId(openId);
        entity.setUnionId(unionId);

        entity.setCreateDate(new Date());
        entity.setUpdateDate(null);

        dao.save(entity);
        return entity;
    }
}
