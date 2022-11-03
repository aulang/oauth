package cn.aulang.oauth.util;

import cn.hutool.crypto.digest.DigestUtil;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-1-5 16:33
 */
public class PasswordUtils {

    public static String digest(String password) {
        return DigestUtil.sha256Hex(password);
    }

    public static String digestAndBcrypt(String password) {
        return DigestUtil.bcrypt(digest(password));
    }

    public static String bcrypt(String passwordSHA256) {
        return DigestUtil.bcrypt(passwordSHA256);
    }

    public static boolean bcryptCheck(String passwordSHA256, String passwordBcrypt) {
        try {
            return DigestUtil.bcryptCheck(passwordSHA256, passwordBcrypt);
        } catch (Exception e) {
            return false;
        }
    }
}
