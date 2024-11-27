//package com.wakedata.dw.openapi;
//
//import com.github.pagehelper.Page;
//import com.google.gson.Gson;
//import com.wakedata.common.core.dto.ResultDTO;
//import com.wakedata.dw.DwOpenWebApplication;
//import com.wakedata.dw.open.controller.business.AppAccessController;
//import com.wakedata.dw.open.enums.ApprovalBusinessTypeEnum;
//import com.wakedata.dw.open.enums.ApprovalStatusEnum;
//import com.wakedata.dw.open.enums.DataAssetEnums;
//import com.wakedata.dw.open.model.api.AuthApiVO;
//import com.wakedata.dw.open.model.query.PageQuery;
//import com.wakedata.dw.open.service.approval.AppApprovalService;
//import com.wakedata.dw.open.userinfo.IpaasUserInfo;
//import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
//import com.wakedata.wd.app.client.app.api.AppRpcService;
//import com.wakedata.wd.app.client.app.dto.AppBaseInfoDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
///**
// * OpenAPI应用Seravice层测试
// * @author 佟蕊
// */
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = DwOpenWebApplication.class)
//@ComponentScan(value = {"com.wakedata.wd.app.client.app.api"})
//@Slf4j
//public class OpenApiAppServiceTest {
//
//    @Autowired
//    private AppRpcService appRpcService;
//
//    private IpaasUserInfo userInfo;
//
//    @Autowired
//    private AppApprovalService appApprovalService;
//
//    @Autowired
//    private AppAccessController appAccessController;
//
//    String toJson(Object obj){
//        return new Gson().toJson(obj);
//    }
//
//    @Before
//    public void setUp() {
//        log.info("启动");
//        userInfo = IpaasUserContext.getUserInfo();
//        if (null == userInfo){
//            IpaasUserInfo ipaasUserInfo = new IpaasUserInfo();
//            ipaasUserInfo.setLesseeId(34L);
//            ipaasUserInfo.setTenantId(1L);
//            userInfo = ipaasUserInfo;
//        }
//        log.info(("user info :"+toJson(userInfo)));
//    }
//
//    @Test
//    public void appRpcTest(){
//        ResultDTO<List<AppBaseInfoDTO>> resultDTO = appRpcService.queryAppBaseInfoByTenantId(userInfo.getTenantId());
//        List<AppBaseInfoDTO> data = resultDTO.getData();
//        Assert.assertTrue(resultDTO.isSuccess());
//        Assert.assertNotNull(data);
//        log.info(toJson(data));
//    }
//
//    @Test
//    public void selectAuthApiListTest(){
//        Long lessessId = 2L;
//        Integer appId = 3181541;
//        PageQuery pageQuery = new PageQuery();
//        List<AuthApiVO> authApiVOList = appApprovalService.selectAuthApiList(null, appId, ApprovalBusinessTypeEnum.APP.getValue(), ApprovalStatusEnum.APPROVAL.getValue(), null, null);
//        Assert.assertNotNull(authApiVOList);
//        log.info(toJson(authApiVOList));
//    }
//
//    @Test
//    public void selectAuthApiListTestWithAuthStatus(){
//        Long lessessId = 34L;
//        Integer appId = 3181546;
//        PageQuery pageQuery = new PageQuery();
//        List<AuthApiVO> authApiVOList = appApprovalService.selectAuthApiListWithAppAuthStatus(lessessId, appId);
//        Assert.assertNotNull(authApiVOList);
//        log.info(toJson(authApiVOList));
//    }
//
//    @Test
//    public void selectAuthApiListTestWithAuthStatusPage(){
//        Long lessessId = 34L;
//        Integer appId = 3181546;
//        PageQuery pageQuery = new PageQuery();
//        List<AuthApiVO> authApiVOList = appApprovalService.selectAuthApiListWithAppAuthStatus(lessessId, appId);
//        Assert.assertNotNull(authApiVOList);
//        log.info(toJson(authApiVOList));
//    }
//
//    @Test
//    public void authCountTest(){
//        Long lessessId = 34L;
//        Integer appId = 3181546;
//        List<AuthApiVO> authApiVOList = appApprovalService.selectAuthApiListWithAppAuthStatus(lessessId, appId);
//        long totalCount = authApiVOList.size();
//        long authedApiCount = authApiVOList.stream().filter(auth -> auth.getAppAuthStatus().equals(DataAssetEnums.AppAuthStatus.AUTHORIZED.getValue())).count();
//        long unAuthApiCount = totalCount - authedApiCount;
//        log.info(String.format("totalCount:%s, authedApiCount:%s, unAuthApiCount%s",totalCount,authedApiCount,unAuthApiCount));
//    }
//
//    @Test
//    public void authListControllerTest(){
//        Integer appId = 3181546;
//        PageQuery pageQuery = new PageQuery();
//        pageQuery.setPageSize(2);
//        pageQuery.setPageNo(1);
//        appAccessController.apiAuthList(appId,pageQuery);
//    }
//}
