package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.repository.ThirdAccountRepository;
import cn.aulang.oauth.thirdserver.core.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:29
 */
@Service
public class ThirdAccountBiz {

    private final AccountBiz accountBiz;
    private final ThirdAccountRepository dao;

    @Autowired
    public ThirdAccountBiz(AccountBiz accountBiz, ThirdAccountRepository dao) {
        this.accountBiz = accountBiz;
        this.dao = dao;
    }

    public ThirdAccount getAccount(Profile profile) {
        String profileId = profile.getId();
        String serverName = profile.getServerName();
        return dao.findByThirdTypeAndThirdId(serverName, profileId);
    }

    public Account register(Profile profile) {
        Account account = new Account();
        account.setNickname(profile.getUsername());
        account = accountBiz.register(account);

        ThirdAccount thirdAccount = new ThirdAccount();
        thirdAccount.setThirdId(profile.getId());
        thirdAccount.setThirdName(profile.getUsername());
        thirdAccount.setAccountId(account.getId());
        thirdAccount.setThirdType(profile.getServerName());

        thirdAccount.setProfile(profile.getOriginInfo());

        dao.save(thirdAccount);

        return account;
    }

    public ThirdAccount bind(String accountId, Profile profile) {
        ThirdAccount thirdAccount = new ThirdAccount();
        thirdAccount.setAccountId(accountId);
        thirdAccount.setThirdId(profile.getId());
        thirdAccount.setThirdName(profile.getUsername());
        thirdAccount.setThirdType(profile.getServerName());
        return dao.save(thirdAccount);
    }
}
