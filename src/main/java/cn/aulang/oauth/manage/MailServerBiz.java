package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.MailServer;
import cn.aulang.oauth.repository.MailServerRepository;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cn.aulang.oauth.common.Constants.DEFAULT_KEY;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-08-14 13:32
 */
@Service
public class MailServerBiz {

    private final MailServerRepository repository;

    @Autowired
    public MailServerBiz(MailServerRepository repository) {
        this.repository = repository;
    }

    private MailServer server = null;
    private final AES aes = SecureUtil.aes(DEFAULT_KEY);

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

        // 解密密码，会有解密失败异常
        server.setPass(aes.decryptStr(server.getPass()));

        return server;
    }
}
