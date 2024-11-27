package com.wakedata.dw.open.utils;

import com.github.pagehelper.Page;

import java.util.List;

/**
 * 分页工具类
 * @author 佟蕊
 */
public class PageUtil {

    public static <T> Page<T> convertToPage(List<T> list, Integer pageNum, Integer pageSize) {
        //创建Page类
        Page page = new Page(pageNum, pageSize);
        //为Page类中的total属性赋值
        page.setTotal(list.size());
        //计算当前需要显示的数据下标起始值
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, list.size());
        //从链表中截取需要显示的子链表，并加入到Page
        page.addAll(list.subList(startIndex, endIndex));
        return page;
    }

}
