package cn.aulang.oauth.common;

/**
 * 登录页
 *
 * @author wulang
 */
public enum LoginPage {

    nh("nh"),
    dh("dh");

    private final String tpl;

    LoginPage(String tpl) {
        this.tpl = tpl;
    }

    public static String pageOf(String tpl, String page) {
        for (LoginPage type : LoginPage.values()) {
            if (type.tpl.equalsIgnoreCase(tpl)) {
                return type.tpl + "/" + page;
            }
        }

        return page;
    }
}
