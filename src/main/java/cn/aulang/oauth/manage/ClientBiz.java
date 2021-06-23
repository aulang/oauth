package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:42
 */
@Slf4j
@Service
public class ClientBiz {
    @Resource
    private ClientRepository dao;

    public Client save(Client entity) {
        return dao.save(entity);
    }

    public Client findOne(String id) {
        return dao.findById(id).orElse(null);
    }

    public Client getClient(String id) throws BaseException {
        return dao.findById(id).orElseThrow(OAuthError.CLIENT_NOT_FOUND::exception);
    }
}
