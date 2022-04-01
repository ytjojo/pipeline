package com.jiulongteng.pipeline;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 集合工具类.
 *
 * @date 17-9-6.
 * @time 下午2:08.
 */
public class CollectionUtils {

    /**
     * 判断集合是否为空.
     *
     * @param collection 需要校验的集合.
     * @return true or false.
     */
    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }

    /**
     * 返回 List 的大小.
     *
     * @param collection 需要查询的集合.
     * @return 0 or size.
     */
    public static int getSize(Collection collection) {
        if (null != collection) {
            return collection.size();
        }
        return 0;
    }



    /**
     * 判断map是否为空.
     *
     * @param map 需要校验的集合.
     * @return true or false.
     */
    public static boolean isEmpty(Map map) {
        return map == null || map.size() == 0;
    }

    /**
     * 返回 map 的大小.
     *
     * @param map 需要查询的集合.
     * @return 0 or size.
     */
    public static int getSize(Map map) {
        if (null != map) {
            return map.size();
        }
        return 0;
    }

}
