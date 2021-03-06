package cn.aulang.oauth.model.bo;

import lombok.Getter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-1-1 20:23
 */
@Getter
public class UnlockAccountDelayed implements Delayed {
    public static final int DEFAULT_LOCKED_MINUTES = 5;

    private String accountId;
    private LocalDateTime unlockTime;

    public UnlockAccountDelayed(String accountId) {
        this(accountId, DEFAULT_LOCKED_MINUTES);
    }

    public UnlockAccountDelayed(String accountId, int lockedMinutes) {
        this.accountId = accountId;
        this.unlockTime = LocalDateTime.now().plusMinutes(lockedMinutes);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Duration.between(LocalDateTime.now(), unlockTime));
    }

    @Override
    public int compareTo(Delayed o) {
        if (!(o instanceof UnlockAccountDelayed)) {
            return 1;
        }

        if (o == this) {
            return 0;
        }

        UnlockAccountDelayed delayed = (UnlockAccountDelayed) o;

        if (this.unlockTime.isBefore(delayed.unlockTime)) {
            return 1;
        } else {
            return -1;
        }
    }
}
