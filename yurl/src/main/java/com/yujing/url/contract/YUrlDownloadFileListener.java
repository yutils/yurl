package com.yujing.url.contract;

import java.io.File;

/**
 * 文件下载监听
 *
 * @author yujing 2019年5月31日15:29:13
 */
public interface YUrlDownloadFileListener {
    void progress(int downloadSize, int fileSize);

    void success(File file);

    void fail(String value);
}