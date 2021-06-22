package cn.aulang.oauth.manage;

import cn.aulang.framework.exception.BaseException;
import cn.aulang.oauth.common.OAuthError;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.exception.AccountLockedException;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.exception.PasswordExpiredException;
import cn.aulang.oauth.model.bo.CaptchaSendResult;
import cn.aulang.oauth.model.bo.Profile;
import cn.aulang.oauth.property.LoginProperties;
import cn.aulang.oauth.repository.AccountRepository;
import cn.aulang.oauth.service.EmailService;
import cn.aulang.oauth.service.SMSService;
import cn.aulang.oauth.util.PasswordUtil;
import cn.hutool.core.util.DesensitizedUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 10:16
 */
@Service
@EnableConfigurationProperties(LoginProperties.class)
public class AccountBiz {
    @Autowired
    private AccountRepository dao;

    @Autowired
    private SMSService smsService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountUnlockBiz unlockBiz;
    @Autowired
    private LoginProperties loginProperties;

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

    public CaptchaSendResult sendCaptcha(String loginName, String captcha) throws BaseException {
        Account account = findByLoginName(loginName);
        if (account == null) {
            throw OAuthError.ACCOUNT_NOT_FOUND.exception();
        }

        String content = StrUtil.format("您申请的验证码是：{}", captcha);

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
            target = DesensitizedUtil.mobilePhone(mobile);
            result = smsService.send(mobile, content);
        } else if (email != null) {
            /**
             * 发送邮件验证码
             */
            /**
             * 隐私处理
             */
            target = DesensitizedUtil.email(email);
            result = emailService.send(email, content);
        } else {
            throw OAuthError.SEND_CAPTCHA_FAILED.exception("账号没有绑定手机和邮箱");
        }

        if (result < 0) {
            throw OAuthError.SEND_CAPTCHA_FAILED.exception();
        }

        return CaptchaSendResult.of(account.getId(), target, captcha, content);
    }

    public Account login(String loginName, String password) throws AuthException {
        Account account = findByLoginName(loginName);

        if (account == null) {
            throw OAuthError.AUTH_ERROR.exception();
        }

        /**
         * 判断账号是否被禁用
         */
        if (Account.DISABLED == account.getStatus()) {
            throw ((AccountLockedException) OAuthError.ACCOUNT_LOCKED.exception()).accountId(account.getId());
        }

        if (!PasswordUtil.bcryptCheck(password, account.getPassword())) {
            int passwordErrorTimes = account.getPasswordErrorTimes();
            account.setPasswordErrorTimes(++passwordErrorTimes);

            /**
             * 密码连续错误一定次锁定账号，5分钟后自动解锁
             */
            if (passwordErrorTimes > loginProperties.getLockAccountTimes()) {
                /**
                 * 禁用账号
                 */
                account.setStatus(Account.DISABLED);
                dao.save(account);

                /**
                 * 延迟解锁
                 */
                unlockBiz.delayUnlock(account.getId());

                throw ((AccountLockedException) OAuthError.ACCOUNT_LOCKED.exception()).accountId(account.getId());
            } else {
                dao.save(account);
                throw OAuthError.AUTH_ERROR.exception();
            }
        }

        if (account.isMustChangePassword()) {
            String reason = account.getMustChangePasswordReason();
            throw ((PasswordExpiredException) OAuthError.PASSWORD_EXPIRED.exception(reason)).accountId(account.getId());
        }

        return account;
    }

    public void changePassword(String id, String password, boolean mustChangePassword) throws BaseException {
        Account account = dao.findById(id).orElseThrow(OAuthError.ACCOUNT_NOT_FOUND::exception);
        account.setPassword(PasswordUtil.bcrypt(password));
        account.setMustChangePassword(mustChangePassword);
        dao.save(account);
    }

    public Account register(Account account) {
        String password = account.getPassword();
        if (password != null) {
            account.setPassword(PasswordUtil.bcrypt(password));
        }
        return dao.save(account);
    }

    public Profile getProfile(String id) throws BaseException {
        Account account = dao.findById(id).orElseThrow(OAuthError.ACCOUNT_NOT_FOUND::exception);

        return new Profile(
                account.getId(),
                account.getNickname(),
                account.getUsername(),
                account.getMobile(),
                account.getEmail()
        );
    }
}
