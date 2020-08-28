package com.yujing.url.contract;

/**
 * 文件下载监听
 *
 * @author yujing 2019年5月31日15:29:13
 */
@Deprecated
public interface YUrlLoadListener {
    void progress(int downloadSize, int size);

    void success(byte[] bytes);

    void fail(String value);
}