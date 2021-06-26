package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.model.bo.Server;
import cn.aulang.oauth.repository.ThirdServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:53
 */
@Service
public class ThirdServerBiz {
    public static List<Server> servers;

    @Autowired
    private AuthStateBiz stateBiz;
    @Autowired
    private ThirdServerRepository dao;

    public List<ThirdServer> findEnabled() {
        return dao.findByEnabledOrderBySortAsc(true);
    }

    public List<Server> getAllServers() {
        if (servers == null) {
            syncServers();
        }
        return servers;
    }

    @Scheduled(cron = "0 0 8-20/1 * * ?")
    private void syncServers() {
        if (servers == null) {
            servers = new CopyOnWriteArrayList<>();
        } else {
            servers.clear();
        }

        findEnabled().forEach(e -> servers.add(new Server(e.getId(), e.getName(), e.getLogoUrl())));
    }

    public ThirdServer save(ThirdServer entity) {
        return dao.save(entity);
    }

    public ThirdServer findOne(String id) {
        return dao.findById(id).orElse(null);
    }

    public ThirdServer findByName(String name) {
        return dao.findByNameAndEnabledIsTrue(name);
    }

    private String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTION);
        params.forEach(
                (k, v) -> builder.append(k)
                        .append(Constants.EQUAL)
                        .append(v)
                        .append(Constants.AND)
        );
        return builder.toString();
    }

    public String buildAuthorizeUrl(String authId, ThirdServer server, String accountId) {
        Map<String, String> params = server.getAuthorizeParams();
        String url = buildGetUrl(server.getAuthorizeUrl(), params);

        StringBuilder authorizeUrl = new StringBuilder(url);

        String state = stateBiz.create(authId, server.getId(), accountId).getId();

        authorizeUrl.append(Constants.STATE).append(Constants.EQUAL).append(state);
        return authorizeUrl.toString();
    }

    public ThirdServer getThirdServer(String id) throws BaseException {
        return dao.findById(id).orElseThrow(OAuthError.THIRD_SERVER_NOT_FOUND::exception);
    }
}
