package cn.aulang.oauth.thirdserver.mini;

import cn.aulang.oauth.common.Constants;
import cn.aulang.oauth.entity.Account;
import cn.aulang.oauth.entity.ThirdAccount;
import cn.aulang.oauth.entity.ThirdServer;
import cn.aulang.oauth.exception.AuthException;
import cn.aulang.oauth.exception.ThirdAccountNotExistException;
import cn.aulang.oauth.manage.ThirdAccountBiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @author wulang
 */
@Service
public class MiniService {

    private final RestTemplate restTemplate;
    private final ThirdAccountBiz thirdAccountBiz;

    @Autowired
    public MiniService(RestTemplate restTemplate, ThirdAccountBiz thirdAccountBiz) {
        this.restTemplate = restTemplate;
        this.thirdAccountBiz = thirdAccountBiz;
    }

    public Account authenticate(ThirdServer server, String code) throws AuthException {
        try {
            String profileUrl = server.getProfileUrl();
            String getUrl = String.format(profileUrl, code);

            String json = restTemplate.getForObject(getUrl, String.class);
            MiniProfile profile = Constants.JSON_MAPPER.readValue(json, MiniProfile.class);
            if (profile == null) {
                throw new AuthException("获取信息为空");
            }

            if (profile.getErrCode() != null) {
                throw new AuthException(profile.getErrMsg());
            }

            profile.setServerId(server.getId());
            profile.setServerType(server.getType());

            ThirdAccount thirdAccount = thirdAccountBiz.getAccount(profile);
            if (thirdAccount != null) {
                Account account = new Account();

                account.setId(thirdAccount.getAccountId());
                account.setNickname(thirdAccount.getThirdName());

                return account;
            } else {
                if (server.getAutoRegister() != null && server.getAutoRegister()) {
                    return thirdAccountBiz.register(profile);
                } else {
                    throw new ThirdAccountNotExistException(server.getId(), profile.getId(), profile.getOpenId(), profile.getUnionId());
                }
            }
        } catch (AuthException ae) {
            throw ae;
        } catch (Exception e) {
            throw new AuthException(server.getName() + "认证失败", e);
        }
    }
}
