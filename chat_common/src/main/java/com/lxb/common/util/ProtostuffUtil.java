package com.lxb.common.util;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.protostuff.LinkedBuffer;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import io.protostuff.ProtostuffIOUtil;

public class ProtostuffUtil {

    /**
     * 序列化
     * @param object
     * @param <T>
     * @return
     */
    public static <T> byte[] serialize(T object) {

        // TODO: unchecked cast
        Class<T> tClass = (Class<T>) object.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = (Schema<T>) getSchema(tClass);
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化
     * @param data
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T deserialize(byte[] data, Class<T> tClass) {
        try {
            T message = tClass.newInstance();
            Schema<T> schema = (Schema<T>) getSchema(tClass);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e){
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    /**
     * Cache schema using Guava Cache
     * @param tClass
     * @param <T>
     * @return
     */
    private static <T> Schema<?> getSchema(Class<T> tClass) {

        CacheLoader<Class<?>, Schema<?>> loader = new CacheLoader<Class<?>, Schema<?>>() {
            @Override
            public Schema<?> load(Class<?> key) throws Exception {
                return RuntimeSchema.createFrom(key);
            }
        };

        LoadingCache<Class<?>, Schema<?>> loadingCache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .build(loader);

        return loadingCache.getUnchecked(tClass);
    }
}
