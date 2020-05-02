package net.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2020/05/02 11:45
 */
@Data
@Document
public class BeiAn implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    /**
     * 工信部备案
     */
    private BeiAnEntry miit;
    /**
     * 公安部备案
     */
    private BeiAnEntry mps;
}
