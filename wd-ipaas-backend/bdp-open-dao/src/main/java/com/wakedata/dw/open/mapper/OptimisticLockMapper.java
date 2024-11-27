package com.wakedata.dw.open.mapper;

import com.wakedata.dw.open.model.OptimisticLockPo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

/**
 * @author tanzhi
 * @title OptimisticLockMapper
 * @projectName bdp-open
 * @date 2019/8/23 10:36
 * @description 基于mysql乐观锁的查询mapper
 */
public interface OptimisticLockMapper extends Mapper<OptimisticLockPo> {


    /**
     * 获取锁
     *
     * @param id
     * @param versionNo
     * @param ip
     * @return
     */
    int getLock(@Param("id") Integer id, @Param("versionNo") Integer versionNo, @Param("ip") String ip);

    /**
     * 释放锁
     *
     * @param id
     * @param versionNo
     * @param ip
     * @return
     */
    int releaseLock(@Param("id") Integer id, @Param("versionNo") Integer versionNo, @Param("ip") String ip);

    /**
     * 是否是死锁
     *
     * @param id
     * @param versionNo
     * @return
     */
    int releaseDeadLock(@Param("id") Integer id, @Param("versionNo") Integer versionNo);

    /**
     * 增加访问次数
     * @return
     */
    int incrementAccessTimes();
}
