package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 14:42
 */
@Service
public class ClientBiz {

    private final ClientRepository dao;

    @Autowired
    public ClientBiz(ClientRepository dao) {
        this.dao = dao;
    }

    public Client save(Client entity) {
        return dao.save(entity);
    }

    public Client getOne() {
        return dao.findFirstByEnabled(true);
    }

    public Client findOne(String id) {
        Optional<Client> optional = dao.findById(id);
        return optional.orElse(null);
    }
}
