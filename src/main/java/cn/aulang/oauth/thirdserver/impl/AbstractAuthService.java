package cn.aulang.oauth.thirdserver.impl;

import cn.aulang.oauth.thirdserver.core.AccessToken;
import cn.aulang.oauth.thirdserver.core.AuthService;
import cn.aulang.oauth.thirdserver.core.Profile;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.manage.ThirdAccountBiz;

/**
 * @author wulang
 */
public abstract class AbstractAuthService implements AuthService {

    @Override
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
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
