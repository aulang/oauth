package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.repository.AuthCodeRepository;
import cn.aulang.oauth.common.OAuthConstants;
import org.apache.commons.lang3.time.DateUtils;
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
        AuthCode authCode = dao.get(code);
        if (authCode == null) {
            return null;
        }

        Date tenMinutesLater = DateUtils.addMinutes(authCode.getCreateDate(), OAuthConstants.DEFAULT_EXPIRES_MINUTES);
        if (tenMinutesLater.before(new Date())) {
            dao.deleteByPrimaryKey(authCode.getId());
            return null;
        }

        dao.deleteByPrimaryKey(authCode.getId());
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
