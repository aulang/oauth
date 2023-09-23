package cn.aulang.oauth.thirdserver.wechat;

import cn.aulang.oauth.thirdserver.impl.AbstractAuthService;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.ThirdAccountBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wulang
 */
@Service
public class WeChatAuthService extends AbstractAuthService {

    public static final String WE_CHAT = "WeChat";

    private final WeChatApi api = new WeChatApi();
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public WeChatAuthService(ThirdAccountBiz thirdAccountBiz, RestTemplate restTemplate) {
        this.thirdAccountBiz = thirdAccountBiz;
        api.setRestTemplate(restTemplate);
    }

    @Override
    public WeChatApi getApi() {
        return api;
    }

    @Override
    public boolean supports(ThirdServer server) {
        return WE_CHAT.equalsIgnoreCase(server.getType());
    }

    @Override
    public ThirdAccountBiz getThirdAccountBiz() {
        return thirdAccountBiz;
    }
}
