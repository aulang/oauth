package cn.aulang.oauth.model.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    private String id;
    private String nickname;
    private String username;
    private String mobile;
    private String email;
}
