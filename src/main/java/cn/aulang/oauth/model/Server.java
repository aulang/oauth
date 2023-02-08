package cn.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wulang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server {

    private String id;
    private String name;
    private String logoUrl;
}