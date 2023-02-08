package cn.aulang.oauth.manage;

import cn.aulang.oauth.common.OAuthConstants;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.repository.AuthCodeRepository;
import cn.hutool.core.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author wulang
 */
@Service
public class AuthCodeBiz {

    private final AuthCodeRepository dao;

    @Autowired
    public AuthCodeBiz(AuthCodeRepository dao) {
        this.dao = dao;
    }


    public AuthCode save(AuthCode entity) {
        dao.save(entity);
        return entity;
    }

    public AuthCode consumeCode(String code) {
        AuthCode authCode = dao.findById(code).orElse(null);
        if (authCode == null) {
            return null;
        }

        Date tenMinutesLater = DateUtil.offsetMinute(authCode.getCreateDate(), OAuthConstants.DEFAULT_EXPIRES_MINUTES);
        if (tenMinutesLater.before(new Date())) {
            dao.deleteById(authCode.getId());
            return null;
        }

        dao.deleteById(authCode.getId());
        return authCode;
    }

    public AuthCode create(String clientId, String redirectUri, String codeChallenge, String accountId) {
        AuthCode code = new AuthCode();
        code.setCodeChallenge(codeChallenge);
        code.setRedirectUri(redirectUri);
        code.setAccountId(accountId);
        code.setClientId(clientId);
        return save(code);
    }
}
