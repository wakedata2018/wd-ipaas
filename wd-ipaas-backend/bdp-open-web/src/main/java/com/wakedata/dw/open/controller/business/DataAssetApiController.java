package com.wakedata.dw.open.controller.business;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.datasource.DataSourceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.wakedata.dw.open.Constants.UserIdentificationParam.KEY_USER_IDENTIFICATION;

/**
 * @author yiyufeng
 * @title DataAssetApiController
 * @projectName bdp-open
 * @date
 * @description
 */
@Slf4j
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/data_asset_api")
@Api(value = "数据资产", tags = "数据资产")
public class DataAssetApiController extends BaseController {

    @Autowired
    private DataAssetApiService dataAssetApiService;
    @Autowired
    private DataAssetService dataAssetService;
//    @Autowired
//    private DwOpenUserService dwOpenUserService;
    @Autowired
    private DataSourceService dataSourceService;

    @ApiOperation("添加负责人")
    @PostMapping(value = "/in_charge/append")
    @AuditLog
    public ResultDTO<Boolean> appendInCharge(
            @RequestParam(KEY_USER_IDENTIFICATION) String userIdentification,
            @RequestParam String appendInChargeEmployeeNumber,
            @RequestParam Integer dataAssetApiId
    ) {
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
//        DwOpenUserDo dwOpenUser = dwOpenUserService.selectByUserName(userIdentification);
//        if (UserTypeEnums.Administrator.getValue().equals(dwOpenUser.getRoleId())) {
//            resultDTO.setData(this.dataAssetApiService.appendInCharge(userIdentification, appendInChargeEmployeeNumber, dataAssetApiId));
//        } else {
//            resultDTO.setFailed(MsgCodeEnum.w_auth_permission_denied);
//        }
        return resultDTO;
    }

    @ApiOperation("移除负责人")
    @PostMapping(value = "/in_charge/remove")
    @AuditLog
    public ResultDTO<Boolean> removeInCharge(
            @RequestParam(KEY_USER_IDENTIFICATION) String userIdentification,
            @RequestParam String removeInChargeEmployeeNumber,
            @RequestParam Integer dataAssetApiId
    ) {
        ResultDTO<Boolean> resultDTO = new ResultDTO<>();
//        DwOpenUserDo dwOpenUser = dwOpenUserService.selectByUserName(userIdentification);
//        if (UserTypeEnums.Administrator.getValue().equals(dwOpenUser.getRoleId())) {
//            resultDTO.setData(this.dataAssetApiService.removeInCharge(userIdentification, removeInChargeEmployeeNumber, dataAssetApiId));
//        } else {
//            resultDTO.setFailed(MsgCodeEnum.w_auth_permission_denied);
//        }
        return resultDTO;
    }


}
