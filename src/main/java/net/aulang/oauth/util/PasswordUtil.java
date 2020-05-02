package net.aulang.oauth.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2020-1-5 16:33
 */
public class PasswordUtil {
    private static byte[] salt = {2, 27, -50, 95, 110, 32, 77, 52, -97, 34, -78, -9, -13, -84, -106, -80};

    private static String sha256Hex(String data, String charset) {
        return new Digester(DigestAlgorithm.SHA256).setSalt(salt).digestHex(data, charset);
    }

    public static String digest(String password) {
        return sha256Hex(password, CharsetUtil.UTF_8);
    }
}
