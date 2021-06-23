package cn.aulang.oauth.controller;

import cn.aulang.framework.web.Response;
import cn.aulang.framework.web.response.ResponseFactory;
import cn.aulang.oauth.entity.AuthCode;
import cn.aulang.oauth.entity.AuthRequest;
import cn.aulang.oauth.entity.Client;
import cn.aulang.oauth.manage.ApprovedScopeBiz;
import cn.aulang.oauth.manage.AuthCodeBiz;
import cn.aulang.oauth.manage.AuthRequestBiz;
import cn.aulang.oauth.manage.ClientBiz;
import cn.aulang.oauth.model.request.ApprovalRequest;
import cn.aulang.oauth.model.response.ApprovedScopeVO;
import cn.aulang.oauth.model.response.AuthCodeVO;
import cn.aulang.oauth.model.response.ScopeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * 授权控制器
 *
 * @author Aulang
 * @date 2021-06-20 15:27
 */
@RestController
@RequestMapping("/api/approval")
public class ApprovalController {
    @Autowired
    private ClientBiz clientBiz;
    @Autowired
    private AuthCodeBiz authCodeBiz;
    @Autowired
    private AuthRequestBiz authRequestBiz;
    @Autowired
    private ApprovedScopeBiz approvedScopeBiz;

    @PostMapping("")
    public Response<AuthCodeVO> approval(@Valid @RequestBody ApprovalRequest request) {
        String authId = request.getAuthId();

        // 检查是否已认证
        AuthRequest authRequest = authRequestBiz.checkAuthenticated(authId);

        // 更新scope
        authRequest.setScopes(new HashSet<>(request.getApproved()));
        authRequestBiz.save(authRequest);

        approvedScopeBiz.approved(authRequest);

        // 创建authorisation code
        AuthCode code = authCodeBiz.create(authRequest);

        // 返回code
        return ResponseFactory.success(
                AuthCodeVO.of(
                        authId,
                        code.getId(),
                        authRequest.getState(),
                        authRequest.getRedirectUri()
                )
        );
    }

    @GetMapping("/{authId}")
    public Response<ApprovedScopeVO> approval(@PathVariable("authId") String authId) {
        AuthRequest authRequest = authRequestBiz.checkAuthenticated(authId);

        Client client = clientBiz.getClient(authRequest.getClientId());

        List<ScopeVO> scopes = new ArrayList<>();
        client.getScopes().forEach((k, v) -> {
            boolean approved = authRequest.getScopes().contains(k);
            ScopeVO vo = ScopeVO.of(k, v, approved);
            scopes.add(vo);
        });

        ApprovedScopeVO vo = new ApprovedScopeVO();

        vo.setAuthId(authId);
        vo.setScopes(scopes);
        vo.setClientId(client.getId());
        vo.setClientName(client.getName());

        return ResponseFactory.success(vo);
    }
}
