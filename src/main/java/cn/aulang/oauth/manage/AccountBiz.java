package cn.aulang.oauth.manage;

import cn.aulang.common.core.utils.Identities;
import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.model.JwtUser;
import cn.aulang.oauth.model.SendCaptchaResult;
import cn.aulang.oauth.repository.AccountRepository;
import cn.aulang.oauth.service.EmailService;
import cn.aulang.oauth.service.SmsService;
import cn.aulang.oauth.util.PasswordUtils;
import cn.hutool.core.util.DesensitizedUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountLockedException;
import java.util.Date;

/**
 * @author wulang
 */
@Service
public class AccountBiz {

    private final AccountRepository dao;
    private final SmsService smsService;
    private final EmailService emailService;
    private final AccountUnlockBiz unlockBiz;

    @Autowired
    public AccountBiz(AccountRepository dao, SmsService smsService, EmailService emailService, AccountUnlockBiz unlockBiz) {
        this.dao = dao;
        this.smsService = smsService;
        this.emailService = emailService;
        this.unlockBiz = unlockBiz;
    }

    public Account save(Account entity) {
        dao.saveOrUpdate(entity);
        return entity;
    }

    public Account getByLoginName(String loginName) {
        return dao.getByUsernameOrMobileOrEmail(loginName);
    }

    public SendCaptchaResult sendCaptcha(String loginName, String captcha) throws RuntimeException {
        Account account = getByLoginName(loginName);
        if (account != null) {
            String content = "您申请的验证码是：" + captcha + "，10分钟内有效";

            String mobile = account.getMobilePhone();
            String email = account.getEmail();

            boolean result;
            String target;
            if (loginName.equals(mobile)) {
                // 发送短信验证码
                // 隐私处理
                target = DesensitizedUtil.mobilePhone(mobile);
                result = smsService.send(mobile, content);
            } else if (loginName.equals(email)) {
                // 发送邮件验证码
                // 隐私处理
                target = DesensitizedUtil.email(email);
                result = emailService.send(email, content);
            } else {
                return null;
            }

            if (!result) {
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
                account.setLockTime(new Date());
                save(account);
                // 延迟解锁
                unlockBiz.delayUnlock(account.getId());
                // 抛出异常
                throw new AccountLockedException("账号被锁定，请稍后再试");
            } else {
                // 报错密码连续错误次数
                save(account);
                // 账号或密码错误
                return null;
            }
        }

        if (account.getMustChpwd()) {
            String reason = account.getChpwdReason();
            if (StringUtils.isBlank(reason)) {
                reason = "请修改密码";
            }
            throw new PasswordExpiredException(reason, account.getId());
        }

        if (triedTimes > Constants.MAX_PASSWORD_ERROR_TIMES) {
            // 登录成功，清除计数器
            account.setTriedTimes(0);
            save(account);
        }

        return account.getId();
    }

    @Transactional(rollbackFor = Exception.class)
    public String changePwd(String id, String password) {
        Account account = dao.get(id);
        if (account != null) {
            String newPassword = PasswordUtils.bcrypt(password);
            dao.updatePassword(id, newPassword, new Date());

            account.setMustChpwd(false);
            account.setLocked(false);
            account.setLockTime(null);
            account.setTriedTimes(0);
            save(account);
            return id;
        }
        return null;
    }

    public Account registerThirdAccount(Account account) {
        account.setId(Identities.uuid2());
        dao.registerThirdAccount(account);
        return account;
    }

    public JwtUser getProfile(String id, String clientId, String tokenId) {
        Account account = dao.get(id);
        if (account == null) {
            return null;
        }

        return new JwtUser(account.getId(),
                account.getUsername(),
                account.getNickname(),
                clientId, tokenId);
    }
}
