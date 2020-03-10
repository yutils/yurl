package com.yujing.url;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * 网络请求工具类
 *
 * @author yujing 2019年5月31日15:54:48
 */
public class YUrlUtils {
    /**
     * 请求map转换成String
     *
     * @param paramsMap paramsMap
     * @return StringBuffer
     */
    public static StringBuffer mapToParams(Map<String, Object> paramsMap) {
        if (paramsMap == null) {
            return null;
        }
        StringBuffer params = new StringBuffer();
        for (Map.Entry<String, Object> element : paramsMap.entrySet()) {
            if (element.getValue() == null)
                continue;
            params.append(element.getKey()).append("=").append(element.getValue()).append("&");
        }
        if (params.length() > 0)
            params.deleteCharAt(params.length() - 1);
        return params;
    }

    /**
     * inputStream 转换成byte[]
     *
     * @param inputStream inputStream
     * @return byte[]
     * @throws IOException IOException
     */
    public static byte[] inputStreamToBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            bs.write(buffer, 0, len);
        }
        bs.flush();
        return bs.toByteArray();
    }
}
