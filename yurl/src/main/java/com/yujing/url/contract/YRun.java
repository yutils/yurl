package com.yujing.url.contract;

/**
 * run回调，解决handle.Post里面有try，代码复制问题
 */
public interface YRun {
    void run() throws Exception;
}
