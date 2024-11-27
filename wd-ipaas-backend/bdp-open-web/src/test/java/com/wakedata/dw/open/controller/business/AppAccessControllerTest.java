package com.wakedata.dw.open.controller.business;

import com.alibaba.fastjson.JSON;
import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.enums.DataAssetEnums.AppType;
import com.wakedata.dw.open.enums.DataAssetEnums.DataAccessAppAuthType;
import com.wakedata.dw.open.vo.DataAccessAppReq;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author wanghu@wakedata.com
 * @date 2021/11/25
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppAccessControllerTest extends DwOpenWebApplicationTest {


    @Test
    public void listDataAccessApp() throws Exception {
        DataAccessAppReq dataAccessAppReq = new DataAccessAppReq();
        dataAccessAppReq.setAppType(AppType.LOW_CODE_APP);
        dataAccessAppReq.setAppType(AppType.LOW_CODE_APP);
        dataAccessAppReq.setAuthType(DataAccessAppAuthType.NO_AUTH);
        dataAccessAppReq.setWakePassword("123456");
        dataAccessAppReq.setWakeUserName("test");
        dataAccessAppReq.setDataAccessAppName("测试低代码平台");
        dataAccessAppReq.setDataAccessDescription("测试低代码");
        dataAccessAppReq.setInCharge("zxt");
        dataAccessAppReq.setLowCodeLogo("ddd");

        mockMvc.perform(
            MockMvcRequestBuilders
                .post("/dw/open/business/data_access_app/create")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(JSON.toJSONString(dataAccessAppReq))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void createDataAccessApp() {
    }

    @Test
    public void editDataAccessApp() {
    }
}
