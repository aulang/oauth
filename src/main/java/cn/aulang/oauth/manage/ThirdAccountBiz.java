package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.ThirdAccountRepository;
import cn.aulang.oauth.server.core.Profile;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:29
 */
@Service
public class ThirdAccountBiz {
    @Autowired
    private AccountBiz accountBiz;
    @Autowired
    private ThirdAccountRepository dao;

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
