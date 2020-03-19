package com.yujing.test;

import com.yujing.url.YUrlAndroid;
import com.yujing.url.contract.YUrlDownloadFileListener;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Test {

    void test1(){


        YUrlAndroid.create().downloadFile("", new File(""), new YUrlDownloadFileListener() {
            @Override
            public void progress(int downloadSize, int fileSize) {

            }

            @Override
            public void success(File file) {

            }

            @Override
            public void fail(String value) {

            }
        });
    }
    void test2(){

    }

}
