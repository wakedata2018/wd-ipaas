package com.wakedata.dw.open.utils;

import org.apache.commons.collections.CollectionUtils;

import java.util.*;
import java.util.function.Function;

/**
 * @author yiyufeng
 * @title CollectionTransformUtils
 * @projectName bdp-open
 * @date
 * @description
 */
public class CollectionTransformUtils {

    public static <K, V> Map<K, V> list2Map(List<V> dataList, Function<V, K> keyFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new HashMap<>(0);
        }
        Map<K, V> map = new HashMap<>(defaultHashMapSize(dataList.size()));
        for (V data : dataList) {
            map.put(keyFunction.apply(data), data);
        }
        return map;
    }

    public static <D, V> Set<V> list2Set(List<D> dataList, Function<D, V> valueFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new HashSet<>(0);
        }
        Set<V> set = new HashSet<>(defaultHashMapSize(dataList.size()));
        for (D data : dataList) {
            set.add(valueFunction.apply(data));
        }
        return set;
    }

    private static int defaultHashMapSize(int size) {
        return size * 4 / 3 + 3;
    }

    public static <K, V, D> Map<K, V> transformMap(List<D> dataList, Function<D, K> keyTransformFunction, Function<D, V> valueTransformFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new HashMap<>(0);
        }
        Map<K, V> map = new HashMap<>(defaultHashMapSize(dataList.size()));
        for (D data : dataList) {
            map.put(keyTransformFunction.apply(data), valueTransformFunction.apply(data));
        }
        return map;
    }

    public static <D, R> List<R> transformList(List<D> dataList, Function<D, R> transformFunction) {
        if (CollectionUtils.isEmpty(dataList)) {
            return new ArrayList<>(0);
        }
        List<R> list = new ArrayList<>(dataList.size());
        for (D data : dataList) {
            list.add(transformFunction.apply(data));
        }
        return list;
    }
}
