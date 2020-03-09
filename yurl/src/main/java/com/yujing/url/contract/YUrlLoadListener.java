package com.yujing.url.contract;

/**
 * 文件加载监听
 *
 * @author yujing 2019年5月31日15:29:13
 */
public interface YUrlLoadListener {
    void progress(int downloadSize, int size);

    void success(byte[] bytes);

    void fail(String value);
}