package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.page.CopyPageReqDTO;
import com.wakedata.dw.lowcode.dto.page.SetCategoryReqDTO;
import com.wakedata.dw.lowcode.dto.page.UpdateLowCodePageDTO;
import com.wakedata.dw.lowcode.service.dto.QueryLowCodePageDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 低代码页面信息
 *
 * @author chenshaopeng
 * @date 2021/11/29
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LowCodePageControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/low/code/page/";

    @Test
    public void add() throws Exception {
        UpdateLowCodePageDTO dto = new UpdateLowCodePageDTO();
        dto.setName("页面名称");
        dto.setTitle("标题");
        dto.setParams("参数");
        dto.setCover("页面封面");
        dto.setCategoryId(1);
        dto.setCompressedContent("压缩内容");
        dto.setContent("内容");
        perform(createJsonReq(URL_PREFIX + ADD, dto));
    }

    @Test
    public void update() throws Exception {
        UpdateLowCodePageDTO dto = new UpdateLowCodePageDTO();
        dto.setName("页面名称2");
        dto.setTitle("标题2");
        dto.setParams("参数2");
        dto.setCover("页面封面2");
        dto.setCategoryId(3);
        dto.setCompressedContent("压缩内容3");
        dto.setContent("内容3");
        dto.setId(6);

        perform(createJsonReq(URL_PREFIX + UPDATE, dto));
    }

    @Test
    public void delete() throws Exception {
        IdDTO idDTO = new IdDTO();
        idDTO.setId(6);
        perform(createJsonReq(URL_PREFIX + DELETE, idDTO));
    }

    @Test
    public void detail() throws Exception {
        IdDTO idDTO = new IdDTO();
        idDTO.setId(6);
        perform(createJsonReq(URL_PREFIX + DETAIL, idDTO));
    }

    @Test
    public void setCategory() throws Exception {
        SetCategoryReqDTO dto = new SetCategoryReqDTO();
        dto.setId(6);
        dto.setCategoryId(4);
        perform(createJsonReq(URL_PREFIX + "/setCategory", dto));
    }

    @Test
    public void copyPage() throws Exception {
        CopyPageReqDTO dto = new CopyPageReqDTO();
        dto.setId(6);
        dto.setName("页面4");
        perform(createJsonReq(URL_PREFIX + "/copyPage", dto));
    }

    @Test
    public void list() throws Exception {
        QueryLowCodePageDTO query = new QueryLowCodePageDTO();
        query.setAppId(1);
        query.setTitle("标题3");

        perform(createJsonReq(URL_PREFIX + LIST, query));
    }

    @Test
    public void existPageName() throws Exception {
        QueryLowCodePageDTO dto = new QueryLowCodePageDTO();
        dto.setName("test");
        dto.setId(16);

        perform(createJsonReq(URL_PREFIX + "exists", dto));
    }
}
