package com.wakedata.dw.openapi;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.controller.business.AppAccessController;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.model.api.AppAccessPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import com.wakedata.dw.open.vo.DataAccessAppReq;
import com.wakedata.wd.app.client.app.dto.AppBaseInfoDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * OpenAPI应用Controller测试
 * @author 佟蕊
 */
@RunWith(SpringRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OpenApiAppMockMVCTest extends DwOpenWebApplicationTest {

    /**
     * 应用管理列表
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#listDataAccessApp(AppAccessPo, PageQuery)}
     * @throws Exception
     */
    @Test
    public void listDataAccessApp() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("pageNo","1");
        paraMaps.add("pageSize","10");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/list/page/like")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 应用详情
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#appDetail(Integer)}
     * @throws Exception
     */
    @Test
    public void appDetail() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("dataAccessAppId","3181541");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/appDetail")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Api详情
     * {@link com.wakedata.dw.open.controller.api.ApiManagerController#assetDetail(Integer)}
     * @throws Exception
     */
    @Test
    public void apiDetail() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("dataAssetId","95");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/asset_detail")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * Api授权列表
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#apiAuthList(Integer, PageQuery)}
     * @throws Exception
     */
    @Test
    public void apiAuthList() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appId","3181541");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/apiAuthList")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 授权接口
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#auth(Integer, Integer)}
     * @throws Exception
     */
    @Test
    public void apiAuth() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appId","1");
        paraMaps.add("apiId","1");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/auth")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 解除授权接口
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#deauth(Integer, Integer)}
     * @throws Exception
     */
    @Test
    public void apiDeauth() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appId","1");
        paraMaps.add("apiId","1");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/deauth")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 删除应用
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#delete(Integer, Boolean)}
     * @throws Exception
     */
    @Test
    public void delete() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("dataAccessAppId","3181527");
        paraMaps.add("forceDelete","false");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 上线/下线
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#updateLine(Integer, Integer)}
     * @throws Exception
     */
    @Test
    public void updateLine() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/updateLine")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .param("dataAccessAppId","3181541")
                                .param("status","0")
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 查询授权应用列表 RPC
     * {@link AppAccessController#queryAppInfo()}
     * @throws Exception
     */
    @Test
    public void queryAppInfo() throws Exception {
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/dw/open/business/data_access_app/queryAppInfo")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 添加应用-JSON方式
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#createDataAccessApp(DataAccessAppReq)}
     * @throws Exception
     */
    @Test
    public void createDataAccessAppJson() throws Exception {
        DataAccessAppReq dataAccessAppReq = new DataAccessAppReq();
        dataAccessAppReq.setAppType(DataAssetEnums.AppType.IPAAS_APP);
        dataAccessAppReq.setAuthType(DataAssetEnums.DataAccessAppAuthType.NO_AUTH);
        dataAccessAppReq.setDataAccessAppName("测试添加应用");
        dataAccessAppReq.setDataAccessDescription("这是个测试添加应用描述");
        dataAccessAppReq.setInCharge("ipaastest");

        dataAccessAppReq.setAppAuthType(DataAssetEnums.AppAuthType.WAKE_CLOUD);

        AppBaseInfoDTO appBaseInfoDTO = new AppBaseInfoDTO();
        appBaseInfoDTO.setId(1L);
        appBaseInfoDTO.setAppName("绿城1");
        appBaseInfoDTO.setTenantId(1L);
        dataAccessAppReq.setApiAuthConfig(new Gson().toJson(appBaseInfoDTO));

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

    /**
     * 添加应用-form-data
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#createDataAccessApp(DataAccessAppReq)}
     * @throws Exception
     */
    @Test
    public void createDataAccessAppPostData() throws Exception {

        AppBaseInfoDTO appBaseInfoDTO = new AppBaseInfoDTO();
        appBaseInfoDTO.setId(1L);
        appBaseInfoDTO.setAppName("绿城45678");
        appBaseInfoDTO.setTenantId(1L);

        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appType",DataAssetEnums.AppType.IPAAS_APP.toString());
        paraMaps.add("authType",(DataAssetEnums.DataAccessAppAuthType.NO_AUTH.toString()));
        paraMaps.add("dataAccessAppName","测试添加应用");
//        paraMaps.add("dataAccessDescription","这是个测试添加应用描述");
        paraMaps.add("inCharge","1");
        paraMaps.add("appAuthType",DataAssetEnums.AppAuthType.WAKE_CLOUD.toString());
        paraMaps.add("apiAuthConfig",new Gson().toJson(appBaseInfoDTO));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/dw/open/business/data_access_app/create")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 编辑应用
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#editDataAccessApp(DataAccessAppReq)}
     * @throws Exception
     */
    @Test
    public void editDataAccessAppPostData() throws Exception {
        AppBaseInfoDTO appBaseInfoDTO = new AppBaseInfoDTO();
        appBaseInfoDTO.setId(1L);
        appBaseInfoDTO.setAppName("绿城hh");
        appBaseInfoDTO.setTenantId(1L);

        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appType",DataAssetEnums.AppType.IPAAS_APP.toString());
//        paraMaps.add("authType",(DataAssetEnums.DataAccessAppAuthType.NO_AUTH.toString()));
        paraMaps.add("dataAccessAppName","测试update应用3");
        paraMaps.add("dataAccessDescription","这是个测试添加应用描述");
        paraMaps.add("inCharge","1");
        paraMaps.add("appAuthType",DataAssetEnums.AppAuthType.WAKE_CLOUD.toString());
        paraMaps.add("apiAuthConfig",new Gson().toJson(appBaseInfoDTO));
        paraMaps.add("dataAccessAppId","3181524");

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/dw/open/business/data_access_app/edit")
                                .contentType(MediaType.APPLICATION_JSON_UTF8)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    /**
     * 根据应用id获取当前的应用配置信息
     * {@link com.wakedata.dw.open.controller.business.AppAccessController#getAuthConfigByAppId(Integer)}
     * @throws Exception
     */
    @Test
    public void getAuthConfigByAppId() throws Exception {
        MultiValueMap<String,String> paraMaps = new LinkedMultiValueMap<String,String>();
        paraMaps.add("appId","31815241");
        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/dw/open/business/data_access_app/getAuthConfigByAppId")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                                .params(paraMaps)
                                .accept(MediaType.APPLICATION_JSON_UTF8)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

}
