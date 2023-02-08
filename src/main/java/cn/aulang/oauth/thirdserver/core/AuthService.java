package cn.aulang.oauth.thirdserver.core;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;

/**
 * 第三方认证服务
 * @author wulang
 */
public interface AuthService {

    Api getApi();

    Account authenticate(ThirdServer server, String code) throws AuthException;

    ThirdAccount bind(ThirdServer server, String code, String accountId) throws AuthException;

    boolean supports(ThirdServer server);
}
