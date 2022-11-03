package cn.aulang.oauth.server.github;

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
public class GitHubAuthService extends AbstractAuthService {

    public static final String GITHUB = "GitHub";

    private final GitHubApi api = new GitHubApi();
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public GitHubAuthService(ThirdAccountBiz thirdAccountBiz) {
        this.thirdAccountBiz = thirdAccountBiz;
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