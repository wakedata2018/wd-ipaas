package com.wakedata.dw.lowcode.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.service.dto.BatchFetchLowCodeInfoDTO;
import com.wakedata.dw.lowcode.dto.component.EditLowCodeComponentDTO;
import com.wakedata.dw.lowcode.dto.component.ExistLowCodeComponentNameDTO;
import java.util.UUID;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 组件控制器单元测试
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/29
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LowCodeComponentControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/dw/open/low/code/component/";

    @Test
    public void add() throws Exception {
        EditLowCodeComponentDTO dto = new EditLowCodeComponentDTO();
        dto.setName(UUID.randomUUID().toString().replace("-", ""));
        dto.setTitle("component title");
        dto.setDesc("desc");
        dto.setMeta("{}");
        dto.setIcon("icon");
        dto.setCover("cover");
        dto.setCompressedContent("{}");
        dto.setContent("{}");
        perform(createJsonReq(URL_PREFIX + ADD, dto));
    }

    @Test
    public void update() throws Exception {
        EditLowCodeComponentDTO dto = new EditLowCodeComponentDTO();
        dto.setId(1);
        dto.setName(UUID.randomUUID().toString().replace("-", ""));
        dto.setTitle("component title for update");
        dto.setDesc("desc for update");
        dto.setMeta("{}");
        dto.setIcon("icon");
        dto.setCover("cover");

        EditLowCodeComponentDTO dto1 = new EditLowCodeComponentDTO();
        dto1.setContent("ddddd");
        dto.setContent(JSON.toJSONString(dto1));

        dto1.setCompressedContent("abcdef");
        dto.setCompressedContent(JSON.toJSONString(dto1));
        perform(createJsonReq(URL_PREFIX + UPDATE, dto));
    }

    @Test
    public void delete() throws Exception {
        IdDTO idDTO = new IdDTO();
        idDTO.setId(2);
        perform(createJsonReq(URL_PREFIX + DELETE, idDTO));
    }

    @Test
    public void detail() throws Exception {
        IdDTO idDTO = new IdDTO();
        idDTO.setId(1);
        perform(createJsonReq(URL_PREFIX + DETAIL, idDTO));
    }

    @Test
    public void existsComponentName() throws Exception {
        ExistLowCodeComponentNameDTO dto = new ExistLowCodeComponentNameDTO();
        dto.setName("5170c19fd4114c7c82575dcadcfe04f2");

        perform(createJsonReq(URL_PREFIX + "existsComponentName", dto));
    }

    @Test
    public void listByBatch() throws Exception {
        BatchFetchLowCodeInfoDTO dto = new BatchFetchLowCodeInfoDTO();
        dto.setName("5170c19fd4114c7c82575dcadcfe04f2");
        perform(createJsonReq(URL_PREFIX + "listByBatch", Lists.newArrayList(dto)));

    }

    @Test
    public void list() throws Exception {
        perform(createFormDataReq(URL_PREFIX + LIST, "appId", "1"));
    }
}
