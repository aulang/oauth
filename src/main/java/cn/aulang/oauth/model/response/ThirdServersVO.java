package cn.aulang.oauth.model.response;

import cn.aulang.oauth.model.bo.Server;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 第三方登录服务
 *
 * @author Aulang
 * @date 2021-06-20 22:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ThirdServersVO {
    private List<Server> servers;
}
