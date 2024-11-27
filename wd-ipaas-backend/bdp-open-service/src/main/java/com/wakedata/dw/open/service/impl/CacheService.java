package com.wakedata.dw.open.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author yiyufeng
 * @title CacheService
 * @projectName bdp-open
 * @date
 * @description
 */
@Service
@Slf4j
@CacheConfig(cacheNames = "caffeine")
public class CacheService {

    private int num = 0;

    @Cacheable(value = "caffeine")
    public int list() {
        log.info("list num {}", ++num);
        return num;
    }

    @CachePut
    public int put() {
        log.info("put num {}", ++num);
        return num;
    }

    @CacheEvict
    public int evit() {
        log.info("evit num {}", ++num);
        return num;
    }

}
