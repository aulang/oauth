package net.aulang.oauth.manage;

import cn.hutool.core.util.StrUtil;
import net.aulang.oauth.common.Constants;
import net.aulang.oauth.entity.Account;
import net.aulang.oauth.exception.PasswordExpiredException;
import net.aulang.oauth.model.CaptchaSendResult;
import net.aulang.oauth.model.Profile;
import net.aulang.oauth.repository.AccountRepository;
import net.aulang.oauth.service.EmailService;
import net.aulang.oauth.service.SMSService;
import net.aulang.oauth.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountLockedException;
import java.util.Optional;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:16
 */
@Service
public class AccountBiz {
    @Autowired
    private AccountRepository dao;

    @Autowired
    private SMSService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountUnlockBiz unlockBiz;

    public Account getOne() {
        return dao.findFirstByStatus(Account.ENABLED);
    }

    public Account save(Account entity) {
        return dao.save(entity);
    }

    public Account findByUsername(String username) {
        return dao.findByUsername(username);
    }

    public Account findByMobile(String mobile) {
        return dao.findByMobile(mobile);
    }

    public Account findByEmail(String email) {
        return dao.findByEmail(email);
    }

    public Account findByLoginName(String loginName) {
        return dao.findByUsernameOrMobileOrEmail(loginName, loginName, loginName);
    }

    public CaptchaSendResult sendCaptcha(String loginName, String captcha) throws RuntimeException {
        Account account = findByLoginName(loginName);
        if (account != null) {
            String content = "您申请的验证码是：" + captcha;

            String mobile = account.getMobile();
            String email = account.getEmail();

            int result;
            String target;
            if (mobile != null && email == null) {
                /**
                 * 发送短信验证码
                 */
                /**
                 * 隐私处理
                 */
                target = mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                result = smsService.send(mobile, content);
            } else if (email != null) {
                /**
                 * 发送邮件验证码
                 */
                /**
                 * 隐私处理
                 */
                target = email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
                result = emailService.send(email, content);
            } else {
                return null;
            }

            if (result < 0) {
                throw new RuntimeException("发送验证码失败");
            }
            return new CaptchaSendResult(null, account.getId(), target);
        }
        return null;
    }

    public String login(String loginName, String password)
            throws PasswordExpiredException, AccountLockedException {
        Account account = findByLoginName(loginName);

        if (account == null) {
            return null;
        }

        /**
         * 判断账号是否被禁用
         */
        if (Account.DISABLED == account.getStatus()) {
            throw new AccountLockedException("账号被锁定，请稍后再试");
        }

        if (!PasswordUtil.bcryptCheck(password, account.getPassword())) {
            int passwordErrorTimes = account.getPasswordErrorTimes();
            account.setPasswordErrorTimes(++passwordErrorTimes);

            /**
             * 密码连续错误一定次锁定账号，5分钟后自动解锁
             */
            if (passwordErrorTimes > Constants.MAX_PASSWORD_ERROR_TIMES) {
                /**
                 * 禁用账号
                 */
                account.setStatus(Account.DISABLED);
                dao.save(account);

                /**
                 * 延迟解锁
                 */
                unlockBiz.delayUnlock(account.getId());

                throw new AccountLockedException("账号被锁定，请稍后再试");
            } else {
                dao.save(account);
                return null;
            }
        }

        if (account.isMustChangePassword()) {
            String reason = account.getMustChangePasswordReason();
            if (StrUtil.isBlank(reason)) {
                reason = "请修改密码";
            }
            throw new PasswordExpiredException(reason, account.getId());
        }

        return account.getId();
    }

    public String changePassword(String id, String password, boolean mustChangePassword) {
        Optional<Account> optional = dao.findById(id);
        if (optional.isPresent()) {
            Account account = optional.get();
            account.setPassword(PasswordUtil.bcrypt(password));
            account.setMustChangePassword(mustChangePassword);
            dao.save(account);
            return id;
        }
        return null;
    }

    public Account register(Account account) {
        String password = account.getPassword();
        if (password != null) {
            account.setPassword(PasswordUtil.bcrypt(password));
        }
        return dao.save(account);
    }

    public Profile getUser(String id) {
        Optional<Account> optional = dao.findById(id);
        if (optional.isEmpty()) {
            return null;
        }
        Account account = optional.get();
        return new Profile(account.getId(), account.getNickname());
    }
}
