package com.wakedata.dw.lowcode.controller;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.lowcode.dto.IdDTO;
import com.wakedata.dw.lowcode.dto.config.AddLowCodeConfigDTO;
import com.wakedata.dw.lowcode.dto.config.QueryLowCodeConfigDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 低代码配置控制层测试
 *
 * @author wanghu@wakedata.com
 * @date 2021/11/29
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LowCodeConfigControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/dw/open/low/code/config/";


    @Test
    public void add() throws Exception {
        AddLowCodeConfigDTO dto = new AddLowCodeConfigDTO();
        dto.setContent("{}");
        dto.setType("runtime");

        perform(createJsonReq(URL_PREFIX + ADD, dto));
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
        QueryLowCodeConfigDTO dto = new QueryLowCodeConfigDTO();

        perform(createJsonReq(URL_PREFIX + LIST, dto));
    }
}
