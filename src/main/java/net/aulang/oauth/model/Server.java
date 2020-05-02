package net.aulang.oauth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/5 9:54
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Server implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String logoUrl;
}