package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.ClientRepository;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wulang
 */
@Service
public class ClientBiz {

    private final ClientRepository dao;

    @Autowired
    public ClientBiz(ClientRepository dao) {
        this.dao = dao;
    }

    @CacheEvict(cacheNames = "client", key = "#result.id")
    public Client save(Client entity) {
        if (StrUtil.isBlank(entity.getId())) {
            entity.setUpdateDate(null);
            entity.setCreateDate(new Date());
        } else {
            entity.setCreateDate(null);
            entity.setUpdateDate(new Date());
        }

        dao.save(entity);
        return entity;
    }

    @Cacheable(cacheNames = "Client", key = "#id")
    public Client get(String id) {
        return dao.findById(id).orElse(null);
    }
}
