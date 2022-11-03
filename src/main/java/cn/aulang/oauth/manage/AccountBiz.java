package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.model.Profile;
import cn.aulang.oauth.model.SendCaptchaResult;
import cn.aulang.oauth.repository.AccountRepository;
import cn.aulang.oauth.service.EmailService;
import cn.aulang.oauth.service.SMSService;
import cn.aulang.oauth.util.PasswordUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
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

    private final AccountRepository dao;
    private final SMSService smsService;
    private final EmailService emailService;
    private final AccountUnlockBiz unlockBiz;

    @Autowired
    public AccountBiz(AccountRepository dao, SMSService smsService, EmailService emailService, AccountUnlockBiz unlockBiz) {
        this.dao = dao;
        this.smsService = smsService;
        this.emailService = emailService;
        this.unlockBiz = unlockBiz;
    }

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

    public SendCaptchaResult sendCaptcha(String loginName, String captcha) throws RuntimeException {
        Account account = findByLoginName(loginName);
        if (account != null) {
            String content = "您申请的验证码是：" + captcha;

            String mobile = account.getMobile();
            String email = account.getEmail();

            int result;
            String target;
            if (mobile != null && email == null) {
                // 发送短信验证码
                // 隐私处理
                target = DesensitizedUtil.mobilePhone(mobile);
                result = smsService.send(mobile, content);
            } else if (email != null) {
                // 发送邮件验证码
                // 隐私处理
                target = DesensitizedUtil.email(email);
                result = emailService.send(email, content);
            } else {
                return null;
            }

            if (result < 0) {
                throw new RuntimeException("发送验证码失败");
            }
            return SendCaptchaResult.of(null, account.getId(), target);
        }
        return null;
    }

    public String login(String loginName, String password)
            throws PasswordExpiredException, AccountLockedException {
        Account account = findByLoginName(loginName);

        if (account == null) {
            return null;
        }

        // 判断账号是否被禁用
        if (Account.DISABLED == account.getStatus()) {
            throw new AccountLockedException("账号被锁定，请稍后再试");
        }

        if (!PasswordUtil.bcryptCheck(password, account.getPassword())) {
            int passwordErrorTimes = account.getPasswordErrorTimes();
            account.setPasswordErrorTimes(++passwordErrorTimes);

            // 密码连续错误一定次锁定账号，5分钟后自动解锁
            if (passwordErrorTimes > Constants.MAX_PASSWORD_ERROR_TIMES) {
                // 禁用账号
                account.setStatus(Account.DISABLED);
                dao.save(account);

                // 延迟解锁
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
