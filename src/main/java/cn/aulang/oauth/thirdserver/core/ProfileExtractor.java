package cn.aulang.oauth.thirdserver.core;

/**
 * 第三方用户信息提取器
 * @author wulang
 */
public interface ProfileExtractor {

    <T extends Profile> T extract(String responseBody, Class<T> type) throws Exception;
}
