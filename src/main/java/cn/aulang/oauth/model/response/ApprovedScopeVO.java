package cn.aulang.oauth.model.response;

import lombok.Data;

import java.util.List;

/**
 * 授权Scope
 *
 * @author Aulang
 * @date 2021-06-20 15:38
 */
@Data
public class ApprovedScopeVO {
    private String authId;
    private String clientId;
    private String clientName;
    private List<ScopeVO> scopes;
}
