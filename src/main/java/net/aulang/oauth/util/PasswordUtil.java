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
    private static String sha256Hex(String data, String charset) {
        return new Digester(DigestAlgorithm.SHA256).digestHex(data, charset);
    }

    public static String digest(String password) {
        return sha256Hex(password, CharsetUtil.UTF_8);
    }
}
