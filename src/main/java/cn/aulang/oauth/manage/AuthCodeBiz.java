package cn.aulang.oauth.manage;

import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.repository.AuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/1 17:02
 */
@Service
public class AuthCodeBiz {
    @Autowired
    private AuthCodeRepository dao;
    @Autowired
    private ApprovedScopeBiz approvedScopeBiz;

    public AuthCode save(AuthCode entity) {
        return dao.save(entity);
    }

    public void delete(String id) {
        dao.deleteById(id);
    }

    public AuthCode findOne(String id) {
        Optional<AuthCode> optional = dao.findById(id);
        return optional.orElse(null);
    }

    public void consumeCode(String code) {
        delete(code);
    }

    public AuthCode create(AuthRequest authRequest) {
        // 判断是否需要用户授权
        approvedScopeBiz.hasApproved(authRequest);
        // 创建Code
        AuthCode code = new AuthCode();
        code.setAuthId(authRequest.getId());
        return save(code);
    }
}
