package com.yujing.url;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 请求结果返回对象监听
 * 【注意：】此处不能用interface只能使用 abstract class，因为要取出泛型<T>的具体实现类型，
 * interface不能取出T类型，
 * 所以只能采用abstract class。
 *
 * @author 余静 2019年11月20日11:01:42
 * @param <T> 泛型
 */

public abstract class YObjectListener<T> {
    private Type type = null;
    protected YObjectListener() {
        //取出泛型具体类型
        Type genericType = getClass().getGenericSuperclass();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            type = parameterizedType.getActualTypeArguments()[0];
        }
    }
    /**
     * 取出泛型的具体类型
     *
     * @return Type
     */
    public Type getType() {
        return type;
    }

    public abstract void success(byte[] bytes, T value);

    public abstract void fail(String value);
}