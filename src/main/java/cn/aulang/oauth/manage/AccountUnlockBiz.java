package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.repository.AccountRepository;
import cn.aulang.oauth.model.UnlockDelayed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wulang
 */
@Slf4j
@Service
public class AccountUnlockBiz implements InitializingBean, DisposableBean {

    private final AccountRepository dao;

    private final DelayQueue<UnlockDelayed> queue = new DelayQueue<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    public AccountUnlockBiz(AccountRepository dao) {
        this.dao = dao;
    }

    public void delayUnlock(String accountId) {
        queue.put(new UnlockDelayed(accountId));
    }

    public void unlock(String accountId) {
        Account account = dao.get(accountId);
        if (account != null) {
            account.setTriedTimes(0);
            account.setLocked(false);
            account.setLockTime(null);
            dao.updateByPrimaryKey(account);
        }
    }

    public void afterPropertiesSet() {
        dao.updateLockedToUnlock();

        executorService.execute(() -> {
            while (true) {
                try {
                    UnlockDelayed delayed = queue.take();
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
