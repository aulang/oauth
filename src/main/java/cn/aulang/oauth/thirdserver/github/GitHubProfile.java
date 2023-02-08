package cn.aulang.oauth.thirdserver.github;

import cn.aulang.oauth.thirdserver.impl.AbstractProfile;

/**
 * @author wulang
 */
public class GitHubProfile extends AbstractProfile {

    private Long id;
    private String name;
    private String login;

    @Override
    public String getId() {
        return id.toString();
    }

    @Override
    public String getUsername() {
        return name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}