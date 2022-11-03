package cn.aulang.oauth.server.qq;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.ThirdAccountBiz;
import cn.aulang.oauth.server.impl.AbstractAuthService;
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

    private final QQApi api = new QQApi();
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public QQAuthService(ThirdAccountBiz thirdAccountBiz) {
        this.thirdAccountBiz = thirdAccountBiz;
    }

    @Override
    public QQApi getApi() {
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