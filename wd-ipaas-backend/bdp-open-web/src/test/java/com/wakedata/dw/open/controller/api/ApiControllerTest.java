package com.wakedata.dw.open.controller.api;

import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.model.api.dto.NotAuthApiReqParam;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * api controller测试类
 *
 * @author wanghu@wakedata.com
 * @date 2021/12/1
 * @since v1.0.0
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ApiControllerTest extends DwOpenWebApplicationTest {

    final String URL_PREFIX = "/dw/open/business/api/";

    @Test
    public void listNotAuthApi() throws Exception {
        NotAuthApiReqParam param = new NotAuthApiReqParam();
        param.setAccessAppId(262);
        param.setApiName("测试");

        perform(createJsonReq(URL_PREFIX + "listNotAuthApi", param));
    }
}
