package com.yujing.url.contract;
/**
 * 请求信息监听
 *
 * @author 余静 2019年5月31日15:28:48
 */
public interface YUrlListener {
    void success(byte[] bytes, String value) throws Exception;

    void fail(String value);
}