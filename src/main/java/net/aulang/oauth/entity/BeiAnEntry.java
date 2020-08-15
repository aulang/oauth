package net.aulang.oauth.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020-05-02 12:03
 */
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class BeiAnEntry implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 备案号
     */
    private String no = "";
    /**
     * 备案查询地址
     */
    private String url = "";
}
