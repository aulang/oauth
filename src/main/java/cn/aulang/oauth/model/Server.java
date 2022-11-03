package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    private String id;
    private String name;
    private String logoUrl;
}