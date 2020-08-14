package net.aulang.oauth.manage;

import net.aulang.oauth.common.Constants;
import net.aulang.oauth.common.OAuthConstants;
import net.aulang.oauth.entity.ThirdServer;
import net.aulang.oauth.model.Server;
import net.aulang.oauth.repository.ThirdServerRepository;
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
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }

    public ThirdServer findByName(String name) {
        return dao.findByNameAndEnabledIsTrue(name);
    }

    private String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTION);
        params.entrySet().forEach(
                e -> builder.append(e.getKey())
                        .append(Constants.EQUAL)
                        .append(e.getValue())
                        .append(Constants.AND)
        );
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
