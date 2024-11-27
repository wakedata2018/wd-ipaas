package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.category.AddLowCodePageCategoryDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 低代码分类信息
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/29
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LowCodePageCategoryControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/dw/open/low/code/category/";

    @Test
    public void add() throws Exception {
        AddLowCodePageCategoryDTO dto = new AddLowCodePageCategoryDTO();
        dto.setName("分类1");

        perform(createJsonReq(URL_PREFIX + ADD, dto));
    }

    @Test
    public void update() throws Exception {
        AddLowCodePageCategoryDTO dto = new AddLowCodePageCategoryDTO();
        dto.setName("分类1");
        dto.setId(1);

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
    public void list() throws Exception {
        perform(createFormDataReq(URL_PREFIX + LIST, "appId", "1"));
    }
}
