package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.ThirdServerRepository;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.model.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wulang
 */
@Service
public class ThirdServerBiz {

    private final AuthStateBiz stateBiz;
    private final ThirdServerRepository dao;

    @Autowired
    public ThirdServerBiz(AuthStateBiz stateBiz, ThirdServerRepository dao) {
        this.stateBiz = stateBiz;
        this.dao = dao;
    }

    public List<ThirdServer> findVisible() {
        Example example = new Example(ThirdServer.class);

        example.and().andEqualTo("visible", true);
        example.orderBy("sort").asc();

        return dao.selectByExample(example);
    }

    @Cacheable(cacheNames = "ThirdServer")
    public List<Server> getAllServers() {
        return findVisible().parallelStream().map(e -> new Server(e.getId(), e.getName(), e.getLogoUrl())).collect(Collectors.toList());
    }

    public ThirdServer save(ThirdServer entity) {
        if (entity.isNew()) {
            entity.setUpdateDate(null);
            entity.setCreateDate(new Date());
        } else {
            entity.setCreateDate(null);
            entity.setUpdateDate(new Date());
        }

        dao.save(entity);
        return entity;
    }

    @Cacheable(cacheNames = "ThirdServer", key = "#id")
    public ThirdServer get(String id) {
        return dao.get(id);
    }

    private String buildGetUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder(url).append(Constants.QUESTION);
        params.forEach((key, value) -> builder.append(key)
                .append(Constants.EQUAL)
                .append(value)
                .append(Constants.AND));
        return builder.toString();
    }

    public String buildAuthorizeUrl(String authorizeId, ThirdServer server, String accountId) throws Exception {
        Map<String, String> authorizeParams = Constants.toMap(server.getAuthorizeParams());
        String url = buildGetUrl(server.getAuthorizeUrl(), authorizeParams);

        StringBuilder authorizeUrl = new StringBuilder(Constants.REDIRECT);
        authorizeUrl.append(url);

        String state = stateBiz.create(authorizeId, server.getId(), accountId).getId();

        authorizeUrl.append(OAuthConstants.STATE).append(Constants.EQUAL).append(state);
        return authorizeUrl.toString();
    }
}
