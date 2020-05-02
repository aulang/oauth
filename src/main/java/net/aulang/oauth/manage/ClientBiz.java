package net.aulang.oauth.manage;

import lombok.extern.slf4j.Slf4j;
import net.aulang.oauth.entity.Client;
import net.aulang.oauth.repository.ClientRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

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

    public Client getOne() {
        return dao.findFirstByEnabled(true);
    }

    public Client findOne(String id) {
        Optional<Client> optional = dao.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            return null;
        }
    }
}
