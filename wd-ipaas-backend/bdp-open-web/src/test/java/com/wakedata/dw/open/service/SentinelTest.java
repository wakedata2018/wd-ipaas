package com.wakedata.dw.open.service;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.wakedata.dw.open.service.utils.SentinelUtils;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author ZhangXueJun
 * @title SentinelTest
 * @date 2021/2/23 16:49
 * @projectName dw-open
 * @description
 */
public class SentinelTest {

    @Test
    public void testReloadLimit() throws InterruptedException {
        int i = 1;
        // 配置规则.
        SentinelUtils.initFlowRules("HelloWorld", i++);

        while (true) {
            // 1.5.0 版本开始可以直接利用 try-with-resources 特性
            try (Entry entry = SphU.entry("HelloWorld")) {
                // 被保护的逻辑
                System.out.println("hello world");
            } catch (BlockException e) {
                SentinelUtils.initFlowRules("HelloWorld", i++);
                // 处理被流控的逻辑
                System.out.println("blocke!!!!!");
                TimeUnit.MILLISECONDS.sleep(1000);
            }
        }
    }

    @Test
    public void testNoLimit() throws InterruptedException {
        try (Entry entry = SphU.entry("HelloWorldx")) {
            // 被保护的逻辑
            System.out.println("hello world");
        } catch (BlockException e) {
            // 处理被流控的逻辑
            System.out.println("blocke!!!!!");
            TimeUnit.MILLISECONDS.sleep(1000);
        }
    }

    @SentinelResource("HelloWorld")
    public void helloWorld() {
        // 资源中的逻辑
        System.out.println("hello world");
    }
}
