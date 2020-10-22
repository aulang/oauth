package cn.aulang.oauth.manage;

import cn.aulang.oauth.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.model.UnlockAccountDelayed;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-1-1 20:56
 */
@Slf4j
@Service
public class AccountUnlockBiz implements DisposableBean {
    @Autowired
    private AccountRepository dao;
    @Autowired
    private MongoTemplate mongoTemplate;

    private DelayQueue<UnlockAccountDelayed> queue = new DelayQueue<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    public void delayUnlock(String accountId) {
        queue.put(new UnlockAccountDelayed(accountId));
    }

    public void unlock(String accountId) {
        dao.findById(accountId).ifPresent(a -> {
            a.setPasswordErrorTimes(0);
            a.setStatus(Account.ENABLED);
            dao.save(a);
        });
    }

    @PostConstruct
    public void init() {
        /**
         * 自动解锁之前锁定的账号
         */
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("status").is(Account.DISABLED)),
                Update.update("status", Account.ENABLED).set("passwordErrorTimes", 0),
                Account.class
        );

        executorService.execute(() -> {
            while (true) {
                try {
                    UnlockAccountDelayed delayed = queue.take();
                    unlock(delayed.getAccountId());
                } catch (Exception e) {
                    log.error("账号锁定后自动解锁失败", e);
                }
            }
        });
    }

    @Override
    public void destroy() {
        executorService.shutdown();
    }
}
