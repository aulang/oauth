package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.MailServerRepository;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.aulang.oauth.entity.MailServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.aulang.oauth.common.Constants.DEFAULT_KEY;

/**
 * @author wulang
 */
@Service
public class MailServerBiz {

    private final MailServerRepository dao;

    @Autowired
    public MailServerBiz(MailServerRepository dao) {
        this.dao = dao;
    }

    private MailServer server = null;
    private final AES aes = SecureUtil.aes(DEFAULT_KEY);

    public MailServer save(MailServer entity) {
        dao.save(entity);

        server.setPassword(aes.decryptStr(server.getPassword()));

        return server;
    }

    public MailServer get() {
        if (server != null) {
            return server;
        }

        List<MailServer> servers = dao.findAll();

        if (CollectionUtil.isEmpty(servers)) {
            return null;
        } else {
            server = servers.get(0);
            // 解密密码，会有解密失败异常
            server.setPassword(aes.decryptStr(server.getPassword()));
        }

        return server;
    }
}
