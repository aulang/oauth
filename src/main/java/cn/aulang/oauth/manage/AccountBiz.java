package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.model.Profile;
import cn.aulang.oauth.model.SendCaptchaResult;
import cn.aulang.oauth.repository.AccountRepository;
import cn.aulang.oauth.service.EmailService;
import cn.aulang.oauth.service.SMSService;
import cn.aulang.oauth.util.PasswordUtils;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountLockedException;

/**
 * @author wulang
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

    public Account save(Account entity) {
        dao.save(entity);
        return entity;
    }

    public Account getByLoginName(String loginName) {
        return dao.findByLoginName(loginName);
    }

    public SendCaptchaResult sendCaptcha(String loginName, String captcha) throws RuntimeException {
        Account account = getByLoginName(loginName);
        if (account != null) {
            String content = "您申请的验证码是：" + captcha;

            String mobile = account.getPhone();
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
        Account account = getByLoginName(loginName);
        if (account == null) {
            return null;
        }

        String passwordBcrypt = account.getPassword();

        // 判断账号是否被禁用
        if (account.getLocked()) {
            throw new AccountLockedException("账号被锁定，请稍后再试");
        }

        int triedTimes = account.getTriedTimes() + 1;

        if (!PasswordUtils.bcryptCheck(password, passwordBcrypt)) {
            account.setTriedTimes(triedTimes);

            // 密码连续错误一定次锁定账号，5分钟后自动解锁
            if (triedTimes > Constants.MAX_PASSWORD_ERROR_TIMES) {
                // 禁用账号
                account.setLocked(true);
                dao.save(account);
                // 延迟解锁
                unlockBiz.delayUnlock(account.getId());
                // 抛出异常
                throw new AccountLockedException("账号被锁定，请稍后再试");
            } else {
                // 报错密码连续错误次数
                dao.save(account);
                // 账号或密码错误
                return null;
            }
        }

        if (account.getMustChpwd()) {
            String reason = account.getChpwdReason();
            if (StrUtil.isBlank(reason)) {
                reason = "请修改密码";
            }
            throw new PasswordExpiredException(reason, account.getId());
        }

        if (triedTimes > Constants.MAX_PASSWORD_ERROR_TIMES) {
            // 登录成功，清除计数器
            account.setTriedTimes(0);
            dao.save(account);
        }

        return account.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public String changePwd(String id, String password) {
        Account account = dao.findById(id).orElse(null);
        if (account != null) {
            String newPassword = PasswordUtils.bcrypt(password);

            account.setPassword(newPassword);
            account.setMustChpwd(false);
            account.setLocked(false);
            account.setTriedTimes(0);
            dao.save(account);
            return id;
        }
        return null;
    }

    public Account register(Account account) {
        dao.save(account);
        return account;
    }

    public Profile getProfile(String id, String clientId) {
        Account account = dao.findById(id).orElse(null);
        if (account == null) {
            return null;
        }

        return new Profile(account.getId(), account.getUsername(), account.getNickname(), clientId);
    }
}
