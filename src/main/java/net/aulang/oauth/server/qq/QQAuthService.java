package net.aulang.oauth.server.qq;

import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.server.core.Api;
import net.aulang.oauth.server.impl.AbstractAuthService;
import net.aulang.oauth.manage.ThirdAccountBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:54
 */
@Service
public class QQAuthService extends AbstractAuthService {
    public static final String QQ = "QQ";

    private QQApi api = new QQApi();
    @Autowired
    private ThirdAccountBiz thirdAccountBiz;

    @Override
    public Api getApi() {
        return api;
    }

    @Override
    public boolean supports(ThirdServer server) {
        return QQ.equals(server.getName());
    }

    @Override
    public ThirdAccountBiz getThirdAccountBiz() {
        return thirdAccountBiz;
    }
}