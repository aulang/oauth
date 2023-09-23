package cn.aulang.oauth.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * @author wulang
 */
public class PasswordUtils {

    public static String bcrypt(String password) {
        return DigestUtil.bcrypt(password);
    }

    public static boolean bcryptCheck(String password, String bcrypt) {
        try {
            return DigestUtil.bcryptCheck(password, bcrypt);
        } catch (Exception e) {
            return false;
        }
    }
}
