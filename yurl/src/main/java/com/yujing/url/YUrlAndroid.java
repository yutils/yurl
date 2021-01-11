package com.yujing.url;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.yujing.url.contract.YObjectListener;
import com.yujing.url.contract.YSessionListener;
import com.yujing.url.contract.YUrlDownloadFileListener;
import com.yujing.url.contract.YUrlListener;
import com.yujing.url.contract.YUrlLoadListener;

import java.io.File;
import java.util.Map;

/**
 * 网络请求类
 *
 * @author yujing 2020年3月16日13:50:01
 * 请使用 YHttp
 */
@Deprecated
public class YUrlAndroid extends YUrl {
    private static final String TAG = "YUrlAndroid";
    private final Handler handler = new Handler(Looper.getMainLooper());

    public static YUrlAndroid create() {
        return new YUrlAndroid();
    }

    @Override
    public YUrlAndroid setContentType(String contentType) {
        super.setContentType(contentType);
        return this;
    }

    @Override
    public YUrlAndroid setConnectTimeout(int connectTimeout) {
        super.setConnectTimeout(connectTimeout);
        return this;
    }

    @Override
    public YUrlAndroid setCrtSSL(String crtSSL) {
        super.setCrtSSL(crtSSL);
        return this;
    }

    @Override
    public YUrlAndroid setRequestProperty(String key, String value) {
        super.setRequestProperty(key, value);
        return this;
    }

    @Override
    public YUrlAndroid addRequestProperty(String key, String value) {
        super.addRequestProperty(key, value);
        return this;
    }

    @Override
    public YUrlAndroid setSessionListener(YSessionListener ySessionListener) {
        super.setSessionListener(ySessionListener);
        return this;
    }

    @Override
    public YUrlAndroid setSessionId(String sessionId) {
        super.setSessionId(sessionId);
        return this;
    }

    /**
     * get请求
     *
     * @param requestUrl url
     * @param listener   监听
     */
    @Override
    public void get(final String requestUrl, final YUrlListener listener) {
        super.get(requestUrl, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                handler.post(new YRunnable(() -> {
                    try {
                        listener.success(bytes, value);
                    } catch (Exception e) {
                        listener.fail("处理异常");
                        e.printStackTrace();
                    }
                }));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }

    /**
     * get请求
     *
     * @param requestUrl url
     * @param listener   监听
     * @param <T>        类型
     */
    @Override
    public <T> void get(String requestUrl, final YObjectListener<T> listener) {
        super.get(requestUrl, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                println("对象转换类型：" + listener.getType());
                handler.post(new YRunnable(() -> {
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
                }));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param request    请求内容
     * @param listener   监听
     */
    @Override
    public void post(String requestUrl, String request, YUrlListener listener) {
        post(requestUrl, request.getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param paramsMap  key，value
     * @param listener   监听
     */
    @Override
    public void post(String requestUrl, Map<String, Object> paramsMap, YUrlListener listener) {
        post(requestUrl, YUrlUtils.mapToParams(paramsMap).toString().getBytes(), listener);
    }

    /**
     * post请求
     *
     * @param requestUrl   url
     * @param requestBytes bytes
     * @param listener     监听
     */
    @Override
    public void post(final String requestUrl, final byte[] requestBytes, final YUrlListener listener) {
        super.post(requestUrl, requestBytes, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                handler.post(new YRunnable(() -> {
                    try {
                        listener.success(bytes, value);
                    } catch (Exception e) {
                        listener.fail("处理异常");
                        e.printStackTrace();
                    }
                }));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }

    /**
     * post请求
     *
     * @param requestUrl url
     * @param paramsMap  key，value
     * @param listener   监听
     * @param <T>        类型
     */
    @Override
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
    @Override
    public <T> void post(final String requestUrl, String params, YObjectListener<T> listener) {
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
    @Override
    public <T> void post(String requestUrl, byte[] requestBytes, final YObjectListener<T> listener) {
        super.post(requestUrl, requestBytes, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                println("对象转换类型：" + listener.getType());
                handler.post(new YRunnable(() -> {
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
                }));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
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
    @Override
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
    @Override
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
    @Override
    public void upload(final String requestUrl, final byte[] requestBytes, final Map<String, File> fileMap, final YUrlListener listener) {
        super.upload(requestUrl, requestBytes, fileMap, new YUrlListener() {
            @Override
            public void success(byte[] bytes, String value) {
                handler.post(new YRunnable(() -> listener.success(bytes, value)));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }

    /**
     * 文件下载,get请求，回调进度
     *
     * @param requestUrl url
     * @param file       保存的文件
     * @param listener   监听
     */
    @Override
    public void downloadFile(final String requestUrl, final File file, final YUrlDownloadFileListener listener) {
        super.downloadFile(requestUrl, file, new YUrlDownloadFileListener() {

            @Override
            public void progress(int downloadSize, int fileSize) {
                handler.post(new YRunnable(() -> listener.progress(downloadSize, fileSize)));
            }

            @Override
            public void success(File file) {
                handler.post(new YRunnable(() -> listener.success(file)));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }

    /**
     * 加载get请求，回调进度
     *
     * @param requestUrl url
     * @param listener   监听
     */
    @Override
    public void load(final String requestUrl, final YUrlLoadListener listener) {
        super.load(requestUrl, new YUrlLoadListener() {
            @Override
            public void progress(int downloadSize, int size) {
                handler.post(new YRunnable(() -> listener.progress(downloadSize, size)));
            }

            @Override
            public void success(byte[] bytes) {
                handler.post(new YRunnable(() -> listener.success(bytes)));
            }

            @Override
            public void fail(String value) {
                handler.post(new YRunnable(() -> listener.fail(value)));
            }
        });
    }


    /**
     * 错误回调
     *
     * @param error    错误
     * @param listener 监听
     */
    @Override
    void error(final String error, final Object listener) {
        handler.post(() -> {
            if (listener instanceof YUrlListener) {
                Log.e(TAG, error);
                ((YUrlListener) listener).fail(error);
            } else if (listener instanceof YUrlLoadListener) {
                Log.e(TAG, error);
                ((YUrlLoadListener) listener).fail(error);
            } else if (listener instanceof YUrlDownloadFileListener) {
                Log.e(TAG, error);
                ((YUrlDownloadFileListener) listener).fail(error);
            }
        });
    }
}
