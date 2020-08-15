package net.aulang.oauth.manage;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import net.aulang.oauth.entity.MailServer;
import net.aulang.oauth.repository.MailServerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static net.aulang.oauth.common.Constants.DEFAULT_KEY;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-08-14 13:32
 */
@Service
public class MailServerBiz {
    @Autowired
    private MailServerRepository repository;

    private MailServer server = null;
    private AES aes = SecureUtil.aes(DEFAULT_KEY);

    public MailServer save(MailServer entity) {
        server = repository.save(entity);

        server.setPass(aes.decryptStr(server.getPass()));

        return server;
    }

    public MailServer get() {
        if (server != null) {
            return server;
        }

        server = repository.findFirstBy();

        if (server == null) {
            server = new MailServer();
            server.setHost("smtp.aliyun.com");
            server.setPort(465);
            server.setSslEnable(true);
            server.setAuth(true);
            server.setUser("aulang@aliyun.com");
            server.setPass(aes.encryptHex("123456"));
            server.setFrom("aulang@aliyun.com");
            return repository.save(server);
        }

        /**
         * 解密密码，会有解密失败异常
         */
        server.setPass(aes.decryptStr(server.getPass()));

        return server;
    }
}
