package com.mtons.mblog.utils;

import com.google.common.cache.*;

import java.util.concurrent.TimeUnit;

public class CacheUtils {
    private static final long GUAVA_CACHE_SIZE =  100000;
    private static final long GUAVA_CACHE_DAY = 30;

    private static LoadingCache<String,String> GLOBAL_CACHE = null;
    static {
        try {
            GLOBAL_CACHE =loadCache(new CacheLoader<String, String>() {
                @Override
                public String load(String aLong) throws Exception {
                    // 处理缓存键不存在缓存值时的处理逻辑
                    return "";
                }
            });
        } catch (Exception e) {

        }
    }
    private static LoadingCache<String, String> loadCache(CacheLoader<String, String> cacheLoader) throws Exception {
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                //缓存池大小，在缓存项接近该大小时， Guava开始回收旧的缓存项
                .maximumSize(GUAVA_CACHE_SIZE)
                //设置时间对象没有被读/写访问则对象从内存中删除(在另外的线程里面不定期维护)
                .expireAfterAccess(GUAVA_CACHE_DAY, TimeUnit.DAYS)
                // 设置缓存在写入之后 设定时间 后失效
                .expireAfterWrite(GUAVA_CACHE_DAY, TimeUnit.DAYS)
                //移除监听器,缓存项被移除时会触发
                .removalListener((RemovalListener<String, String>) rn -> {
                    //逻辑操作
                })
                //开启Guava Cache的统计功能
                .recordStats()
                .build(cacheLoader);
        return cache;
    }
    public static void put(String key, String value) {
        try {
            GLOBAL_CACHE.put(key, value);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String get(String key) {
        String token = "";
        try {
            token = GLOBAL_CACHE.get(key);
        } catch (Exception e) {
            System.out.println(e);
        }
        return token;
    }
    public static void remove(String key) {
        try {
            GLOBAL_CACHE.invalidate(key);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static void removeAll() {
        try {
            GLOBAL_CACHE.invalidateAll();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static long size() {
        long size = 0;
        try {
            size = GLOBAL_CACHE.size();
        } catch (Exception e) {
            System.out.println(e);
        }
        return size;
    }
}
