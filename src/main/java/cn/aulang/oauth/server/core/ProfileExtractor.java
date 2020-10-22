package cn.aulang.oauth.server.core;

/**
 * @author Aulang
 * @email aulang@qq.com
 * @date 2019-12-7 17:15
 * 第三方用户信息提取器
 */
public interface ProfileExtractor {
    <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception;
}
