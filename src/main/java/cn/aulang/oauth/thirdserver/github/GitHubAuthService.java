package cn.aulang.oauth.thirdserver.github;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.manage.ThirdAccountBiz;
import cn.aulang.oauth.thirdserver.impl.AbstractAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wulang
 */
@Service
public class GitHubAuthService extends AbstractAuthService {

    public static final String GITHUB = "GitHub";

    private final GitHubApi api = new GitHubApi();
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public GitHubAuthService(ThirdAccountBiz thirdAccountBiz, RestTemplate restTemplate) {
        this.thirdAccountBiz = thirdAccountBiz;
        api.setRestTemplate(restTemplate);
    }

    @Override
    public GitHubApi getApi() {
        return api;
    }

    @Override
    public boolean supports(ThirdServer server) {
        return GITHUB.equals(server.getName());
    }

    @Override
    public ThirdAccountBiz getThirdAccountBiz() {
        return thirdAccountBiz;
    }
}