package com.yujing.url;

import com.yujing.url.contract.YUrlProgressListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Objects;

/**
 * 网络请求基础类，柱塞式
 *
 * @author yujing 2019年5月31日15:52:55
 */
public class YUrlBase {
    private String contentType = "application/x-www-form-urlencoded;charset=utf-8";
    private int connectTimeout = 1000 * 20;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    //crt证书
    private String crtSSL;

    public String getCrtSSL() {
        return crtSSL;
    }

    public void setCrtSSL(String crtSSL) {
        this.crtSSL = crtSSL;
    }

    /**
     * get请求
     *
     * @param requestUrl url
     * @return 请求结果
     * @throws Exception 异常
     */
    public byte[] get(String requestUrl) throws Exception {
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = YHttpURLConnectionFactory.create(requestUrl, crtSSL);
        // 设置连接主机超时时间
        urlConn.setConnectTimeout(connectTimeout);
        //设置从主机读取数据超时
        urlConn.setReadTimeout(connectTimeout);
        // 设置是否使用缓存  默认是true
        urlConn.setUseCaches(false);
        // 设置为Post请求
        urlConn.setRequestMethod("GET");
        //设置客户端与服务连接类型
        urlConn.setRequestProperty("accept", "*/*");
        urlConn.setRequestProperty("connection", "Keep-Alive");
        urlConn.setRequestProperty("Charset", "utf-8");
        urlConn.setRequestProperty("Content-Type", contentType);
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        int responseCode = urlConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("错误码：" + responseCode);
        }
        byte[] bytes = YUrlUtils.inputStreamToBytes(urlConn.getInputStream());
        // 关闭连接
        urlConn.disconnect();
        return bytes;
    }

    /**
     * post请求
     *
     * @param requestUrl   rul
     * @param requestBytes 请求内容
     * @return 请求结果
     * @throws Exception 异常
     */
    public byte[] post(String requestUrl, byte[] requestBytes) throws Exception {
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = YHttpURLConnectionFactory.create(requestUrl, crtSSL);
        // 设置连接超时时间
        urlConn.setConnectTimeout(connectTimeout);
        //设置从主机读取数据超时
        urlConn.setReadTimeout(connectTimeout);
        //设置请求允许输入 默认是true
        urlConn.setDoInput(true);
        // Post请求不能使用缓存
        urlConn.setUseCaches(false);
        // 设置为Post请求
        urlConn.setRequestMethod("POST");
        //设置本次连接是否自动处理重定向
        urlConn.setInstanceFollowRedirects(true);
        // 配置请求Content-Type
        urlConn.setRequestProperty("accept", "*/*");
        urlConn.setRequestProperty("connection", "Keep-Alive");
        urlConn.setRequestProperty("Charset", "utf-8");
        urlConn.setRequestProperty("Content-Type", contentType);//"application/x-www-form-urlencoded;charset=utf-8";
        // 发送POST请求必须设置如下两行
        urlConn.setDoOutput(true);
        urlConn.setDoInput(true);
        // 开始连接
        urlConn.connect();
        // 发送请求参数
        DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
        dos.write(requestBytes);
        dos.flush();
        dos.close();
        // 判断请求是否成功
        int responseCode = urlConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("错误码：" + responseCode);
        }
        byte[] bytes = YUrlUtils.inputStreamToBytes(urlConn.getInputStream());
        // 关闭连接
        urlConn.disconnect();
        return bytes;
    }

    /**
     * 文件下载
     *
     * @param requestUrl url
     * @param file       下载的文件
     * @param listener   进度监听
     * @throws Exception 异常
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void downloadFile(String requestUrl, File file, YUrlProgressListener listener) throws Exception {
        File parent = file.getParentFile();
        if (!Objects.requireNonNull(parent).exists()) {
            parent.mkdirs();
        }
        if (file.exists()) {
            file.delete();// 删除存在文件
        }
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = YHttpURLConnectionFactory.create(requestUrl, crtSSL);
        // 设置连接主机超时时间
        urlConn.setConnectTimeout(connectTimeout);
        urlConn.setAllowUserInteraction(true);
        // 设置是否使用缓存
        urlConn.setUseCaches(false);
        //设置客户端与服务连接类型
        urlConn.addRequestProperty("Connection", "Keep-Alive");
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        int responseCode = urlConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("错误码：" + responseCode);
        }
        int downloadSize = 0;
        int fileSize = urlConn.getContentLength(); // 获取不到文件大小时候fileSize=-1
        int len;
        byte[] buffer = new byte[1024 * 8];
        FileOutputStream fos = new FileOutputStream(file);
        InputStream inputStream = urlConn.getInputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            // 写到本地
            fos.write(buffer, 0, len);
            downloadSize += len;
            listener.progress(downloadSize, fileSize);
        }
        // 关闭连接
        urlConn.disconnect();
    }

    /**
     * 加载
     *
     * @param requestUrl url
     * @param listener   进度监听
     * @return 结果
     * @throws Exception 异常
     */
    public byte[] load(String requestUrl, YUrlProgressListener listener) throws Exception {
        // 打开一个HttpURLConnection连接
        HttpURLConnection urlConn = YHttpURLConnectionFactory.create(requestUrl, crtSSL);
        // 设置连接主机超时时间
        urlConn.setConnectTimeout(connectTimeout);
        urlConn.setAllowUserInteraction(true);
        // 设置是否使用缓存
        urlConn.setUseCaches(false);
        // 设置为Post请求
        urlConn.setRequestMethod("GET");
        //设置客户端与服务连接类型
        urlConn.setRequestProperty("accept", "*/*");
        urlConn.setRequestProperty("connection", "Keep-Alive");
        urlConn.setRequestProperty("Charset", "utf-8");
        urlConn.setRequestProperty("Content-Type", contentType);//"application/x-www-form-urlencoded;charset=utf-8";
        // 开始连接
        urlConn.connect();
        // 判断请求是否成功
        int responseCode = urlConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("错误码：" + responseCode);
        }
        int downloadSize = 0;
        int fileSize = urlConn.getContentLength(); // 获取不到文件大小时候fileSize=-1
        int len;
        byte[] buffer = new byte[1024 * 8];
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        InputStream inputStream = urlConn.getInputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
            downloadSize += len;
            listener.progress(downloadSize, fileSize);
        }
        bs.flush();
        byte[] bytes = bs.toByteArray();
        // 关闭连接
        urlConn.disconnect();
        return bytes;
    }
}
