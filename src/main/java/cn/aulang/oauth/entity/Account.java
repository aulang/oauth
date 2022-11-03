package cn.aulang.oauth.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Aulang
 * @email aulang@aq.com
 * @date 2019/12/2 14:28
 */
@Data
@Document
public class Account implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final int ENABLED = 1;
    public static final int DISABLED = 0;

    @Id
    private String id;

    /**
     * 用户名
     */
    @Indexed(unique = true, sparse = true)
    private String username;
    /**
     * 登录手机
     */
    @Indexed(unique = true, sparse = true)
    private String mobile;
    /**
     * 登录邮箱
     */
    @Indexed(unique = true, sparse = true)
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 是否需要修改密码
     */
    boolean mustChangePassword = false;

    /**
     * 需要修改密码的理由
     */
    private String mustChangePasswordReason = "密码已过期，请修改密码！";

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 密码连续错误次数
     */
    private int passwordErrorTimes = 0;
    /**
     * 账号状态
     */
    private int status = ENABLED;

    private LocalDateTime createdDateTime = LocalDateTime.now();
}
