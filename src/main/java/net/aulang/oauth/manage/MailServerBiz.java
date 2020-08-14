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

    public MailServer save(MailServer server) {
        return repository.save(server);
    }

    public MailServer get() {
        if (server != null) {
            return server;
        }

        server = repository.findFirstBy();

        AES aes = SecureUtil.aes(DEFAULT_KEY);

        if (server == null) {
            server = MailServer.of(
                    "smtp.aliyun.com",
                    465,
                    true,
                    true,
                    "aulang@aliyun.com",
                    aes.encryptHex("123456"),
                    "aulang@aliyun.com");
        }

        /**
         * 解密密码
         */
        server.setPass(aes.decryptStr(server.getPass()));

        return repository.save(server);
    }
}
