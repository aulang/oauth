package cn.aulang.oauth.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Scope
 *
 * @author Aulang
 * @date 2021-06-20 15:40
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ScopeVO {
    private String code;
    private String name;
    private Boolean approved;
}
