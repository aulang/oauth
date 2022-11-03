package cn.aulang.oauth.server.github;

import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.server.core.AccessToken;
import cn.aulang.oauth.server.impl.AbstractApi;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-12-07 16:41
 */
public class GitHubApi extends AbstractApi<GitHubProfile> {

    @Override
    public void getDetail(ThirdServer server, AccessToken accessToken, GitHubProfile profile) {

    }
}
