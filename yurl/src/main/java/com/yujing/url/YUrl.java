package com.yujing.url;

import android.util.Log;

import com.google.gson.Gson;
import com.yujing.url.contract.YObjectListener;
import com.yujing.url.contract.YSessionListener;
import com.yujing.url.contract.YUrlDownloadFileListener;
import com.yujing.url.contract.YUrlListener;
import com.yujing.url.contract.YUrlLoadListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Map;

/**
 * 网络请求类
 *
 * @author yujing 2020年3月16日13:50:08
 * 请使用 YHttp
 */
@Deprecated
public class YUrl extends YUrlBase {
    private static final String TAG = "YUrl";
    static volatile boolean showLog = true;

    public static void setShowLog(boolean showLog) {
        YUrl.showLog = showLog;
    }

    @Override
    public YUrl setContentType(String contentType) {
        super.setContentType(contentType);
        return this;
    }

    @Override
    public YUrl setConnectTimeout(int connectTimeout) {
        super.setConnectTimeout(connectTimeout);
        return this;
    }

    @Override
    public YUrl setCrtSSL(String crtSSL) {
        super.setCrtSSL(crtSSL);
        return this;
    }

    @Override
    public YUrl setRequestProperty(String key, String value) {
        super.setRequestProperty(key, value);
        return this;
    }

    @Override
    public YUrl addRequestProperty(String key, String value) {
        super.addRequestProperty(key, value);
        return this;
    }

    @Override
    public YUrl setSessionListener(YSessionListener ySessionListener) {
        super.setSessionListener(ySessionListener);
        return this;
    }

    @Override
    public YUrl setSessionId(String sessionId) {
        super.setSessionId(sessionId);
        return this;
    }

    /**
     * get请求
     *
     * @param requestUrl url
     * @param listener   监听
     */
    public void get(final String requestUrl, final YUrlListener listener) {
        Thread thread = new Thread(() -> {
            try {
                if (showLog) println("请求地址：Post--->" + requestUrl);
                byte[] bytes = get(requestUrl);
                String result = new String(bytes);
                if (showLog) println("请求结果：" + result);
                listener.success(bytes, result);
            } catch (Exception e) {
                exception(e, listener);
            } finally {
                YUrlThreadPool.shutdown();
            }
        });
        YUrlThreadPool.add(thread);
    }

    /**
     * get请求
     *
     * @param requestUrl url
     * @param listener   监听
     * @param <T>        类型
     */
    public <T> void get(final String requestUrl, final YObjectListener<T> listener) {
        get(requestUrl, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                println("对象转换类型：" + listener.getType());
                try {
                    if (String.class.equals(listener.getType())) {
                        listener.success(bytes, (T) value);
                    } else if ("byte[]".equals(listener.getType().toString())) {
                        listener.success(bytes, (T) bytes);
                    } else {
                        Gson gson = new Gson();
                        T object = gson.fromJson(value, listener.getType());
                        listener.success(bytes, object);
                    }
                } catch (java.lang.ClassCastException e) {
                    listener.fail("对象转换失败");
                    e.printStackTrace();
                } catch (Exception e) {
                    listener.fail("处理异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String value) {
                listener.fail(value);
            }
        });
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param paramsMap  key，value
     * @param listener   监听
     */
    public void post(final String requestUrl, Map<String, Object> paramsMap, final YUrlListener listener) {
        post(requestUrl, YUrlUtils.mapToParams(paramsMap).toString().getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param params     文本
     * @param listener   监听
     */
    public void post(final String requestUrl, String params, final YUrlListener listener) {
        post(requestUrl, params.getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl   url
     * @param requestBytes bytes
     * @param listener     监听
     */
    public void post(final String requestUrl, final byte[] requestBytes, final YUrlListener listener) {
        Thread thread = new Thread(() -> {
            try {
                if (showLog) println("请求地址：Post--->" + requestUrl);
                if (showLog && requestBytes != null) println("请求参数：" + new String(requestBytes));
                byte[] bytes = post(requestUrl, requestBytes);
                String result = new String(bytes);
                if (showLog) println("请求结果：" + result);
                listener.success(bytes, result);
            } catch (Exception e) {
                exception(e, listener);
            } finally {
                YUrlThreadPool.shutdown();
            }
        });
        YUrlThreadPool.add(thread);
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param paramsMap  key，value
     * @param listener   监听
     * @param <T>        类型
     */
    public <T> void post(String requestUrl, Map<String, Object> paramsMap, YObjectListener<T> listener) {
        post(requestUrl, YUrlUtils.mapToParams(paramsMap).toString().getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param params     文本
     * @param listener   监听
     * @param <T>        类型
     */
    public <T> void post(String requestUrl, String params, YObjectListener<T> listener) {
        post(requestUrl, params.getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl   url
     * @param requestBytes bytes
     * @param listener     监听
     * @param <T>          类型
     */
    public <T> void post(String requestUrl, byte[] requestBytes, final YObjectListener<T> listener) {
        post(requestUrl, requestBytes, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                println("对象转换类型：" + listener.getType());
                try {
                    if (String.class.equals(listener.getType())) {
                        listener.success(bytes, (T) value);
                    } else if ("byte[]".equals(listener.getType().toString())) {
                        listener.success(bytes, (T) bytes);
                    } else {
                        Gson gson = new Gson();
                        T object = gson.fromJson(value, listener.getType());
                        listener.success(bytes, object);
                    }
                } catch (java.lang.ClassCastException e) {
                    listener.fail("对象转换失败");
                    e.printStackTrace();
                } catch (Exception e) {
                    listener.fail("处理异常");
                    e.printStackTrace();
                }
            }

            @Override
            public void fail(String value) {
                listener.fail(value);
            }
        });
    }

    /**
     * 文件上传post
     *
     * @param requestUrl url
     * @param paramsMap  key，value
     * @param fileMap    文件列表
     * @param listener   监听
     */
    public void upload(final String requestUrl, Map<String, Object> paramsMap, Map<String, File> fileMap, final YUrlListener listener) {
        upload(requestUrl, YUrlUtils.mapToParams(paramsMap).toString().getBytes(), fileMap, listener);
    }

    /**
     * 文件上传post
     *
     * @param requestUrl url
     * @param params     文本
     * @param fileMap    文件列表
     * @param listener   监听
     */
    public void upload(final String requestUrl, String params, Map<String, File> fileMap, final YUrlListener listener) {
        upload(requestUrl, params.getBytes(), fileMap, listener);
    }

    /**
     * 文件上传post
     *
     * @param requestUrl   url
     * @param requestBytes bytes
     * @param fileMap      文件列表
     * @param listener     监听
     */
    public void upload(final String requestUrl, final byte[] requestBytes, final Map<String, File> fileMap, final YUrlListener listener) {
        Thread thread = new Thread(() -> {
            try {
                if (showLog)
                    println("文件上传开始：\nupload--->" + requestUrl + (requestBytes == null ? "" : ("\n文件数：" + fileMap.size() + "\n请求参数：" + new String(requestBytes))));
                byte[] bytes = upload(requestUrl, requestBytes, fileMap);
                String result = new String(bytes);
                if (showLog) println("文件上传完成：" + result);
                listener.success(bytes, result);
            } catch (Exception e) {
                exception(e, listener);
            } finally {
                YUrlThreadPool.shutdown();
            }
        });
        YUrlThreadPool.add(thread);
    }

    /**
     * 文件下载,get请求，回调进度
     *
     * @param requestUrl url
     * @param file       保存的文件
     * @param listener   监听
     */
    public void downloadFile(final String requestUrl, final File file, final YUrlDownloadFileListener listener) {
        Thread thread = new Thread(() -> {
            try {
                if (showLog) println("文件下载开始：\nGet--->" + requestUrl);
                downloadFile(requestUrl, file, listener::progress);
                if (showLog)
                    println("文件下载完成：\nGet--->" + requestUrl + "\n保存路径：" + file.getPath());
                listener.success(file);
            } catch (Exception e) {
                exception(e, listener);
            } finally {
                YUrlThreadPool.shutdown();
            }
        });
        YUrlThreadPool.add(thread);
    }

    /**
     * 加载get请求，回调进度
     *
     * @param requestUrl url
     * @param listener   监听
     */
    public void load(final String requestUrl, final YUrlLoadListener listener) {
        Thread thread = new Thread(() -> {
            try {
                if (showLog)
                    println("文件加载开始：\nGet--->" + requestUrl);
                byte[] bytes = load(requestUrl, listener::progress);
                if (showLog)
                    println("文件加载完成");
                listener.success(bytes);
            } catch (Exception e) {
                exception(e, listener);
            } finally {
                YUrlThreadPool.shutdown();
            }
        });
        YUrlThreadPool.add(thread);
    }

    /**
     * 错误情况处理
     *
     * @param e        错误
     * @param listener 监听
     */
    void exception(Exception e, Object listener) {
        if (e instanceof MalformedURLException) {
            error("URL地址不规范", listener);
        } else if (e instanceof java.net.SocketTimeoutException) {
            error("网络连接超时", listener);
        } else if (e instanceof UnsupportedEncodingException) {
            error("不支持的编码", listener);
        } else if (e instanceof FileNotFoundException) {
            error("找不到该地址", listener);
        } else if (e instanceof IOException) {
            error("连接服务器失败", listener);
        } else {
            error("请求失败 " + e.getMessage(), listener);
        }
    }

    /**
     * 错误回调
     *
     * @param error    错误
     * @param listener 监听
     */
    void error(String error, Object listener) {
        printlnE(error);
        if (listener instanceof YUrlListener) {
            ((YUrlListener) listener).fail(error);
        } else if (listener instanceof YUrlLoadListener) {
            ((YUrlLoadListener) listener).fail(error);
        } else if (listener instanceof YUrlDownloadFileListener) {
            ((YUrlDownloadFileListener) listener).fail(error);
        }
    }

    /**
     * 打印日志。如果发现包含Log就用Log打印，否则就用println
     *
     * @param str 日志
     */
    void println(String str) {
        try {
            Class.forName("android.util.Log");
            println("YUrl", str);
        } catch (Exception e) {
            System.out.println(str);
        }
    }
    /**
     * 打印错误日志。如果发现包含Log就用Log打印，否则就用println
     * @param str 错误内容
     */
    void printlnE(String str) {
        try {
            Class.forName("android.util.Log");
            Log.e("YHttp", str);
        } catch (Exception e) {
            System.err.println(str);
        }
    }
    /**
     * 打印日志
     *
     * @param TAG tag
     * @param msg 内容
     */
    private static void println(String TAG, String msg) {
        int LOG_MAX_LENGTH = 2000;
        int strLength = msg.length();
        int start = 0;
        int end = LOG_MAX_LENGTH;
        for (int i = 0; i < 100; i++) {
            //剩下的文本还是大于规定长度则继续重复截取并输出
            if (strLength > end) {
                String tag = TAG + " " + i;
                Log.d(tag, msg.substring(start, end));
                start = end;
                end = end + LOG_MAX_LENGTH;
            } else {
                String tag = i == 0 ? TAG : TAG + " " + i;
                Log.d(tag, msg.substring(start, strLength));
                break;
            }
        }
    }
}
