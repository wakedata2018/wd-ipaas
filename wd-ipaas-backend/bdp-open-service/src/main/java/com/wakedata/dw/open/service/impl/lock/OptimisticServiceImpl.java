package com.wakedata.dw.open.service.impl.lock;

import com.wakedata.dw.open.mapper.OptimisticLockMapper;
import com.wakedata.dw.open.service.lock.OptimisticLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author tanzhi
 * @title OptimisticServiceImpl
 * @projectName bdp-open
 * @date 2019/8/23 14:12
 * @description
 */
@Service
@Slf4j
public class OptimisticServiceImpl implements OptimisticLockService {

    @Autowired
    private OptimisticLockMapper optimisticLockMapper;

    @Override
    public boolean getLock(Integer id, Integer versionNo, String ip) {
        int lock = optimisticLockMapper.getLock(id, versionNo, ip);
        if (lock == 1) {

            log.info("获取到锁");
            return true;

        } else {
            log.info("未获取到锁");
            return false;
        }
    }

    @Override
    public boolean releaseLock(Integer id, Integer versionNo, String ip) {
        int lock = optimisticLockMapper.releaseLock(id, versionNo, ip);
        if (lock == 1) {
            log.info("已释放锁");
            return true;
        } else {
            log.info("未成功释放锁");
            return false;
        }

    }

    @Override
    public boolean releaseDeadLock(Integer id, Integer versionNo) {
        int lock = optimisticLockMapper.releaseDeadLock(id, versionNo);
        if (lock == 1) {
            log.info("已成功释放死锁");
            return true;
        } else {
            log.info("未成功释放死锁");
            return false;
        }
    }

    @Override
    public boolean isDeadLock(Date lockTime, long period) {
        long now = System.currentTimeMillis();
        long time = lockTime.getTime();
        if (now - time > period * 3) {
            log.info("检测到死锁");
            return true;
        }
        log.info("未检测到死锁");
        return false;
    }


}
