package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.model.Server;
import cn.aulang.oauth.repository.ThirdServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:53
 */
@Service
public class ThirdServerBiz {
    public static List<Server> servers;

    private final AuthStateBiz stateBiz;
    private final ThirdServerRepository dao;

    @Autowired
    public ThirdServerBiz(AuthStateBiz stateBiz, ThirdServerRepository dao) {
        this.stateBiz = stateBiz;
        this.dao = dao;
    }

    public List<ThirdServer> findEnabled() {
        return dao.findByEnabledOrderBySortAsc(true);
    }

    public List<Server> getAllServers() {
        if (servers == null) {
            syncServers();
        }
        return servers;
    }

    @Scheduled(cron = "0 0 6-22/1 * * ?")
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

    public ThirdServer getOne() {
        return dao.findFirstByEnabled(true);
    }

    public ThirdServer findOne(String id) {
        Optional<ThirdServer> optional = dao.findById(id);
        return optional.orElse(null);
    }

    public ThirdServer findByName(String name) {
        return dao.findByNameAndEnabledIsTrue(name);
    }

    private String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTION);
        params.forEach((key, value) -> builder.append(key)
                .append(Constants.EQUAL)
                .append(value)
                .append(Constants.AND));
        return builder.toString();
    }

    public String buildAuthorizeUrl(String authorizeId, ThirdServer server, String accountId) {
        Map<String, String> params = server.getAuthorizeParams();
        String url = buildGetUrl(server.getAuthorizeUrl(), params);

        StringBuilder authorizeUrl = new StringBuilder(Constants.REDIRECT);
        authorizeUrl.append(url);

        String state = stateBiz.create(authorizeId, server.getId(), accountId).getId();

        authorizeUrl.append(OAuthConstants.STATE).append(Constants.EQUAL).append(state);
        return authorizeUrl.toString();
    }
}
