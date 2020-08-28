package com.yujing.url.contract;

/**
 * sessionId 监听
 * 返回值为新的sessionId
 * @author 余静 2020年6月19日16:25:56
 */
@Deprecated
public interface YSessionListener {
    void sessionId(String sessionId);
}