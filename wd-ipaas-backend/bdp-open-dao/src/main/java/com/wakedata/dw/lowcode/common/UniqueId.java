package com.wakedata.dw.lowcode.common;

import java.util.Date;

/**
 * @author wanghu@wakedata.com
 * @title 唯一标识
 * @date 2021/12/1
 * @since v1.0.0
 */
public interface UniqueId {

    /**
     * 获取更新时间
     *
     * @return 更新日期
     */
    Date getUpdateTime();

    /**
     * 获取标识
     *
     * @return 标识名称
     */
    String getName();
}
