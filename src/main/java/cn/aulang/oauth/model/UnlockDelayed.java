package cn.aulang.oauth.model;

import lombok.Getter;
import org.springframework.lang.NonNull;

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
public class UnlockDelayed implements Delayed {

    public static final int DEFAULT_LOCKED_MINUTES = 5;

    private final String accountId;
    private final LocalDateTime unlockTime;

    public UnlockDelayed(String accountId) {
        this(accountId, DEFAULT_LOCKED_MINUTES);
    }

    public UnlockDelayed(String accountId, int lockedMinutes) {
        this.accountId = accountId;
        this.unlockTime = LocalDateTime.now().plusMinutes(lockedMinutes);
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(Duration.between(LocalDateTime.now(), unlockTime));
    }

    @Override
    public int compareTo(@NonNull Delayed other) {
        if (!(other instanceof UnlockDelayed delayed)) {
            return 1;
        }

        if (other == this) {
            return 0;
        }

        if (this.unlockTime.isBefore(delayed.unlockTime)) {
            return 1;
        } else {
            return -1;
        }
    }
}
