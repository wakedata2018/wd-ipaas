package com.wakedata.dw;

import com.alibaba.fastjson.JSON;
import javax.servlet.http.Cookie;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author tanzhi
 * @title DwOpenWebApplicationTest
 * @date 2019/11/22 15:05
 * @projectName dw-open
 * @descriptor
 */
@SpringBootTest(classes = DwOpenWebApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("wdtest")
public class DwOpenWebApplicationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    private static final String TOKEN = "a47c2a7ce18f21ddd6b85f430eeb48c9e597b3f9e3d8ed0163ced"
        + "0f121d1925f59b70e131caee976e52196b84cb44152";

    public static final String APP_ID = "261";


    private static final Cookie COOKIE = new Cookie("token", TOKEN);

    private static final Cookie NEW_COOKIE = new Cookie("dpjsid", "c780bfab-e08f-479b-a9f2-779e75b77e43");

    public static final String ADD = "add";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String DETAIL = "detail";
    public static final String LIST = "list";

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        System.out.println("启动");
    }

    protected RequestBuilder createJsonReq(String url, Object dto) {
        return MockMvcRequestBuilders
            .post(url)
            .contentType(MediaType.APPLICATION_JSON_UTF8)
            .header("App-Id", APP_ID)
            .cookie(NEW_COOKIE)
            .content(JSON.toJSONString(dto))
            .accept(MediaType.APPLICATION_JSON_UTF8);
    }

    protected RequestBuilder createFormDataReq(String url, String key, String... value) {
        return MockMvcRequestBuilders
            .post(url)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .header("App-Id", APP_ID)
            .cookie(NEW_COOKIE)
            .param(key, value)
            .accept(MediaType.APPLICATION_JSON_UTF8);
    }

    protected RequestBuilder createFormDataReq(String url, MultiValueMap<String, String> params) {
        return MockMvcRequestBuilders
            .post(url)
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .cookie(NEW_COOKIE)
            .header("App-Id", APP_ID)
            .params(params)
            .accept(MediaType.APPLICATION_JSON_UTF8);
    }

    protected void perform(RequestBuilder requestBuilder) throws Exception {
        mockMvc.perform(requestBuilder)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(MockMvcResultHandlers.print());
    }


}