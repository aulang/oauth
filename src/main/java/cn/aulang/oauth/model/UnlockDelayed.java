package cn.aulang.oauth.model;

import cn.hutool.core.date.DateUtil;
import lombok.Getter;
import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * @author wulang
 */
@Getter
public class UnlockDelayed implements Delayed {

    public static final int DEFAULT_LOCKED_MINUTES = 5;

    private final String accountId;
    private final Date unlockTime;

    public UnlockDelayed(String accountId) {
        this(accountId, DEFAULT_LOCKED_MINUTES);
    }

    public UnlockDelayed(String accountId, int lockedMinutes) {
        this.accountId = accountId;
        this.unlockTime = DateUtil.offsetMinute(new Date(), lockedMinutes);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long sourceDuration = new Date().getTime() - unlockTime.getTime();
        return unit.convert(sourceDuration, TimeUnit.MICROSECONDS);
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        if (!(other instanceof UnlockDelayed delayed)) {
            return 1;
        }

        if (other == this) {
            return 0;
        }

        if (unlockTime.before(delayed.unlockTime)) {
            return 1;
        } else {
            return -1;
        }
    }
}
