package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.block.EditLowCodeBlockDTO;
import com.wakedata.dw.lowcode.dto.block.PageLowCodeBlockDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 区块控制器单元测试
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/29
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LowCodeBlockControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/low/code/block/";

    @Test
    public void add() throws Exception {
        EditLowCodeBlockDTO dto = new EditLowCodeBlockDTO();
        dto.setName("testBlock11");
        dto.setDescription("test descriptionDDD");
        dto.setDsl("{}");
        dto.setSnapshot("snp");

        perform(createJsonReq(URL_PREFIX + ADD, dto));
    }

    @Test
    public void update() throws Exception {
        EditLowCodeBlockDTO dto = new EditLowCodeBlockDTO();
        dto.setId(1);
        dto.setName("testBlockForUpdate");
        dto.setDescription("update test description");
        dto.setDsl("{}");
        dto.setSnapshot("snp");

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
        PageLowCodeBlockDTO dto = new PageLowCodeBlockDTO();
        dto.setName("testBlock11");
        perform(createJsonReq(URL_PREFIX + LIST, dto));
    }
}
