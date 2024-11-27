package com.wakedata.dw.open.service.lock;

import java.util.Date;

/**
 * @author tanzhi
 * @title OptimisticLockService
 * @projectName bdp-open
 * @date 2019/8/23 14:11
 * @description
 */
public interface OptimisticLockService {


    /**
     * 获取锁
     *
     * @param id
     * @param versionNo
     * @param ip
     * @return
     */
    boolean getLock(Integer id, Integer versionNo, String ip);

    /**
     * 释放锁
     *
     * @param id
     * @param versionNo
     * @param ip
     * @return
     */
    boolean releaseLock(Integer id, Integer versionNo, String ip);

    /**
     * 释放死锁
     *
     * @param id
     * @param versionNo
     * @return
     */
    boolean releaseDeadLock(Integer id, Integer versionNo);

    /**
     * 是否死锁
     *
     * @param lockTime
     * @param period
     * @return
     */
    boolean isDeadLock(Date lockTime, long period);
}
