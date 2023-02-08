package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.repository.ThirdAccountRepository;
import cn.aulang.oauth.thirdserver.core.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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
        return dao.findByThirdTypeAndThirdId(profile.getServerName(), profile.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public Account register(Profile profile) {
        String thirdId = profile.getId();
        String thirdName = profile.getUsername();
        String thirdType = profile.getServerName();
        String username = thirdType + "-" + thirdId;

        Account account = new Account();
        account.setUsername(username);
        account.setNickname(thirdName);
        account = accountBiz.register(account);

        ThirdAccount entity = new ThirdAccount();

        entity.setThirdId(thirdId);
        entity.setThirdType(thirdType);
        entity.setThirdName(thirdName);
        entity.setAccountId(account.getId());
        entity.setProfile(profile.getOriginInfo());

        entity.setUpdateDate(null);
        entity.setCreateDate(new Date());

        dao.save(entity);

        return account;
    }

    public ThirdAccount bind(String accountId, Profile profile) {
        ThirdAccount entity = new ThirdAccount();

        entity.setAccountId(accountId);
        entity.setThirdId(profile.getId());
        entity.setThirdName(profile.getUsername());
        entity.setThirdType(profile.getServerName());

        entity.setUpdateDate(new Date());
        entity.setCreateDate(null);

        dao.save(entity);
        return entity;
    }
}
