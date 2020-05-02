package net.aulang.oauth.server.impl;

import net.aulang.oauth.entity.Account;
import net.aulang.oauth.entity.ThirdAccount;
import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.exception.AuthException;
import net.aulang.oauth.server.core.AccessToken;
import net.aulang.oauth.server.core.AuthService;
import net.aulang.oauth.server.core.Profile;
import net.aulang.oauth.manage.ThirdAccountBiz;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:26
 */
public abstract class AbstractAuthService implements AuthService {
    @Override
    public Account authenticate(ThirdServer server, String code) throws AuthException {
        try {
            AccessToken accessToken = getApi().getAccessToken(server, code);
            Profile profile = getApi().getProfile(server, accessToken);

            ThirdAccount thirdAccount = getThirdAccountBiz().getAccount(profile);
            if (thirdAccount != null) {
                Account account = new Account();

                account.setId(thirdAccount.getAccountId());
                account.setNickname(thirdAccount.getThirdName());

                return account;
            } else {
                getApi().getDetail(server, accessToken, profile);
                return getThirdAccountBiz().register(profile);
            }
        } catch (Exception e) {
            throw new AuthException(server.getName() + "认证失败", e);
        }
    }

    @Override
    public ThirdAccount bind(ThirdServer server, String code, String accountId) throws AuthException {
        try {
            AccessToken accessToken = getApi().getAccessToken(server, code);
            Profile profile = getApi().getProfile(server, accessToken);
            getApi().getDetail(server, accessToken, profile);
            return getThirdAccountBiz().bind(accountId, profile);
        } catch (Exception e) {
            throw new AuthException(server.getName() + "绑定失败", e);
        }
    }

    public abstract ThirdAccountBiz getThirdAccountBiz();
}
