package com.wakedata.dw.open.service.api.impl;

import com.github.pagehelper.Page;
import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.model.log.AccessLogPo;
import com.wakedata.dw.open.service.log.AccessLogService;
import com.wakedata.dw.open.utils.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * @author tanzhi
 * @title AccessLogServiceImplTest
 * @date 2019/11/22 19:37
 * @projectName dw-open
 * @descriptor
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class AccessLogServiceImplTest<T> extends DwOpenWebApplicationTest {

    @Autowired
    private AccessLogService accessLogService;

    @Resource
    private RestTemplateUtil restTemplateUtil;

    @Test
    public void testRestTempUtil(){
        String url = restTemplateUtil.buildRequestUrl("xxx/aaaa", "111", "111");
        log.info("url:{}",url);
    }


    @Test
    public void Test001_add() {
        AccessLogPo accessLogPo = new AccessLogPo();
        accessLogPo.setIp("123.234.45.56");
        accessLogPo.setElapsed(5);
        accessLogPo.setResultCode(200);
        accessLogPo.setVersion("1.0");
        accessLogPo.setResultRow(200);
        accessLogPo.setSeqNo("234242432");
        accessLogPo.setCreateTime(new Date());
        accessLogPo.setUpdateTime(new Date());
        accessLogPo.setDataAssetApiId(26);
        accessLogService.add(accessLogPo);
    }


    @Test
    public void Test008_delete() {
        int delete = accessLogService.delete(1);
        assertEquals(1, delete);
    }


    @Test
    public void Test004_update() {
        AccessLogPo accessLogPo = new AccessLogPo();
        accessLogPo.setIp("123.234.45.56");
        accessLogPo.setElapsed(5);
        accessLogPo.setResultCode(200);
        accessLogPo.setVersion("1.0");
        accessLogPo.setResultRow(200);
        accessLogPo.setSeqNo("111111");
        accessLogPo.setCreateTime(new Date());
        accessLogPo.setUpdateTime(new Date());
        accessLogPo.setDataAssetApiId(26);
        accessLogService.update(accessLogPo);
    }

    @Test
    public void Test003_show() {
        AccessLogPo show = accessLogService.show(1);
        assertEquals(1, show.getId().intValue());
    }

    @Test
    public void Test002_find() {
        AccessLogPo accessLogPo = new AccessLogPo();
        accessLogPo.setDataAssetApiId(26);
        List<AccessLogPo> accessLogPos = accessLogService.find(accessLogPo);
        assertEquals(1, accessLogPos.size());
    }


    @Test
    public void Test005_find() {
        AccessLogPo accessLogPo = new AccessLogPo();
        accessLogPo.setDataAssetApiId(26);
        Page<AccessLogPo> accessLogPos = accessLogService.find(accessLogPo, 1, 10);
        assertEquals(1, accessLogPos.getTotal());
    }

    @Test
    public void Test006_like() {
        Page<AccessLogPo> like = accessLogService.like(AccessLogPo.class, Arrays.asList("accessMethod"), "bcdefg", 1, 10);
        assertEquals(1, like.size());
    }


    @Test
    public void Test007_selectPageLikeOrderBy() {
        Page<AccessLogPo> accessLogPos = accessLogService.selectPageLikeOrderBy(new AccessLogPo(), 1, 10, "23.234", Arrays.asList("ip"), "CREATE_TIME", false, "createTime", null, null,null,null);

        assertEquals(1, accessLogPos.size());
    }


}