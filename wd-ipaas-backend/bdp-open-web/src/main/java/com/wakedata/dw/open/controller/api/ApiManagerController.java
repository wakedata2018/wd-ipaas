package com.wakedata.dw.open.controller.api;

import cn.hutool.core.collection.CollUtil;
import com.google.gson.Gson;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.service.api.ApiGroupService;
import com.wakedata.dw.open.service.api.DataApiGatewayService;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.dto.ApiGroupDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author tanzhi
 * @title ApiManagerController
 * @projectName bdp-open
 * @date 2019/8/27 17:50
 * @description
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/")
@Api(value = "API管理", tags = "API管理")
@Slf4j
public class ApiManagerController {


    @Autowired
    private Gson gson;
    @Autowired
    private DataApiGatewayService dataApiGatewayService;

    @Resource
    private ApiGroupService apiGroupService;

    @Resource
    private DataAssetApiService dataAssetApiService;

    /**
     * api导出功能，把所有数据资产以json数据的形式导出
     * 包括所有的调用路径，方法名，请求参数，示例，返回参数等等
     *
     * @param response
     * @return
     */
    @GetMapping(value = "/export", produces = "text/html;charset=utf-8")
    @ApiOperation("API导出")
    public String export(HttpServletResponse response) {
        //fastjson不能够序列化这么多的数据
        return gson.toJson(dataApiGatewayService.export());
    }

    @ApiOperation(value = "API详情（查看api）")
    @GetMapping(value = "/asset_detail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dataAssetId", value = "接口id", required = true),
            @ApiImplicitParam(name = "timestamp", value = "时间戳", required = true),
            @ApiImplicitParam(name = "isApiTest", value = "是否测试", required = false)
    })
    @AuditLog
    public ResultDTO<Map<String, Object>> assetDetail(@RequestParam("dataAssetId") Integer dataAssetId, @RequestParam("timestamp") String timestamp
            ,@RequestParam(name = "isApiTest",defaultValue = "false",required = false)Boolean isApiTest) {
        return dataApiGatewayService.apiDetail(dataAssetId, timestamp,isApiTest);
    }


    /**
     * 获取sdk，暂时获取所有的，后续看业务要求修改
     * @return
     */
    @ApiOperation(value = "获取sdk，暂时获取所有的，后续看业务要求修改",hidden = true)
    @GetMapping("/getSdkApi")
    public ResultDTO<List<ApiGroupDTO>> getSdkApi(Integer testApiGroupId){
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        if(!userInfo.getIsPlatformAdmin()){
            throw new OpenException("无效的账号信息");
        }
        List<ApiGroupDTO> apiGroupDTOList = apiGroupService.getContainApiGroupList(userInfo.getLesseeId());
        if(CollUtil.isEmpty(apiGroupDTOList)){
            return ResultDTO.success(null);
        }
        for(ApiGroupDTO apiGroupDTO : apiGroupDTOList){
             if(testApiGroupId == null || apiGroupDTO.getId().equals(testApiGroupId)){
                 apiGroupDTO.setPublishApiList(dataAssetApiService.getPublishApiListByGroupId(apiGroupDTO.getId(), userInfo.getLesseeId()));
             }
        }
        return ResultDTO.success(apiGroupDTOList);
    }



}