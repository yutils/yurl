package com.yujing.url;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * 网络请求复杂类，柱塞式
 *
 * @author yujing 2019年6月14日09:45:24
 */
public class YUrlComplex {
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
     * boundary就是request头和上传文件内容的分隔符
     */
    protected static final String BOUNDARY = "------------YuJing---------------";

    /**
     * 文件上传
     *
     * @param requestUrl   url
     * @param requestBytes 请求内容
     * @param fileMap      文件
     * @return 请求结果
     * @throws Exception 异常
     */
    public byte[] upload(String requestUrl, byte[] requestBytes, Map<String, File> fileMap) throws Exception {
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
        sendFile(urlConn, requestBytes, fileMap);
        // 判断请求是否成功
        int responseCode = urlConn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("请求失败，错误码：" + responseCode);
        }
        byte[] bytes = YUrlUtils.inputStreamToBytes(urlConn.getInputStream());
        // 关闭连接
        urlConn.disconnect();
        return bytes;
    }

    /**
     * 发送文件,将其要发送的内容和文件转换为byte[]并且发送
     *
     * @param httpURLConnection url
     * @param requestBytes      请求内容
     * @param fileMap           请求文件
     * @throws IOException 异常
     */
    protected void sendFile(HttpURLConnection httpURLConnection, byte[] requestBytes, Map<String, File> fileMap) throws IOException {
        //安卓6.0下使用谷歌浏览器67
        //Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Mobile Safari/537.36
        //iPhoneX下使用谷歌浏览器67
        //Mozilla/5.0 (iPhone; CPU iPhone OS 10_3 like Mac OS X) AppleWebKit/602.1.50 (KHTML, like Gecko) CriOS/56.0.2924.75 Mobile/14E5239e Safari/602.1
        //win10下使用谷歌浏览器67
        //Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/67.0.3396.62 Safari/537.36");
        httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        OutputStream out = new DataOutputStream(httpURLConnection.getOutputStream());
        // 文本参数
        if (requestBytes != null) {
            out.write(requestBytes);
        }
        // 文件
        if (fileMap != null) {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                File file = entry.getValue();
                if (file == null)
                    continue;
                if (!file.exists()) {
                    System.err.println(file.getPath() + "(系统找不到指定的文件。)");
                    throw new FileNotFoundException(file.getPath() + "(系统找不到指定的文件。)");
                }
                StringBuilder strBuf = new StringBuilder();
                strBuf.append("\r\n--").append(BOUNDARY).append("\r\n")
                        .append("Content-Disposition: form-data; name=\"")
                        .append(entry.getKey())
                        .append("\"; filename=\"")
                        .append(file.getName())
                        .append("\"\r\n");
                if (file.getName().lastIndexOf(".png") != -1) {
                    strBuf.append("Content-Type:image/png" + "\r\n\r\n");
                } else if (file.getName().lastIndexOf(".jpg") != -1 || file.getName().lastIndexOf(".jpeg") != -1) {
                    strBuf.append("Content-Type:image/jpeg" + "\r\n\r\n");
                } else {
                    strBuf.append("Content-Type:application/octet-stream" + "\r\n\r\n");
                }
                out.write(strBuf.toString().getBytes());
                DataInputStream in = new DataInputStream(new FileInputStream(file));
                int bytes;
                byte[] bufferOut = new byte[1024 * 8];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                in.close();
            }
        }
        out.write(("\r\n--" + BOUNDARY + "--\r\n").getBytes());// 结束标记
        out.flush();
        out.close();
    }
}
