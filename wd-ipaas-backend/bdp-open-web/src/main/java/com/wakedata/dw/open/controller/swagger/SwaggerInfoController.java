package com.wakedata.dw.open.controller.swagger;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import com.wakedata.dw.open.service.swagger.SwaggerApiService;
import com.wakedata.dw.open.service.swagger.SwaggerInfoService;
import com.wakedata.dw.open.service.swagger.dto.SwaggerInfoQueryDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Swagger信息管理控制器
 *
 * @author chenshaopeng
 * @date 2021/11/2
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/swagger")
@Api(value = "Swagger管理", tags = "Swagger信息管理")
@Validated
@Slf4j
public class SwaggerInfoController extends BaseController {

    @Resource
    private SwaggerInfoService swaggerInfoService;

    @Resource
    private SwaggerApiService swaggerApiService;


    @ApiOperation("添加Swagger信息")
    @PostMapping(value = "/add")
    @AuditLog
    public ResultDTO<SwaggerInfoPo> add(@RequestBody @Valid SwaggerInfoPo swaggerInfo) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        swaggerInfo.setCreateUser(userInfo.getUserIdentification());
        swaggerInfo.setLesseeId(userInfo.getLesseeId());
        swaggerInfoService.addSwaggerInfo(swaggerInfo);
        return show(swaggerInfo.getId());
    }

    @AuditLog
    @ApiOperation("添加Swagger信息，通过上传swagger文件的方式")
    @PostMapping(value = "/add.from.file")
    public ResultDTO<SwaggerInfoPo> addFromFile(@RequestBody SwaggerInfoPo swaggerInfo) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        swaggerInfo.setCreateUser(userInfo.getUserIdentification());
        swaggerInfo.setLesseeId(userInfo.getLesseeId());
        return ResultDTO.success(swaggerInfoService.addSwaggerInfoFromFile(swaggerInfo));
    }

    @ApiOperation("修改Swagger信息")
    @AuditLog
    @PostMapping(value = "/update")
    public ResultDTO<SwaggerInfoPo> update(@RequestBody @Valid SwaggerInfoPo swaggerInfo) {
        swaggerInfoService.updateSwaggerInfo(swaggerInfo);
        return show(swaggerInfo.getId());
    }

    @AuditLog
    @ApiOperation("修改Swagger信息，通过上传swagger文件的方式")
    @PostMapping(value = "/update.from.file")
    public ResultDTO<SwaggerInfoPo> updateFromFile(@RequestBody SwaggerInfoPo swaggerInfo) {
        return ResultDTO.success(swaggerInfoService.updateSwaggerInfoFromFile(swaggerInfo));
    }

    @PostMapping("/delete")
    @ApiOperation("删除Swagger信息")
    @AuditLog
    public ResultDTO<Object> delete(@NotNull(message = "swaggerInfoId不能为空") Integer swaggerInfoId) {
        swaggerInfoService.deleteSwaggerInfo(swaggerInfoId);
        return new ResultDTO<>();
    }

    @GetMapping("/show")
    @ApiOperation("查询Swagger信息")
    public ResultDTO<SwaggerInfoPo> show(@NotNull(message = "swaggerInfoId不能为空") Integer swaggerInfoId) {
        SwaggerInfoPo swaggerInfo = swaggerInfoService.detail(swaggerInfoId);
        ResultDTO<SwaggerInfoPo> resultDTO = new ResultDTO<>();
        resultDTO.setData(swaggerInfo);
        return resultDTO;
    }

    @GetMapping("/list/page/like")
    @ApiOperation("查询Swagger信息列表")
    @ApiImplicitParam(name = "swaggerInfoId", value = "swagger id", required = true)
    @AuditLog
    public PageResultDTO<List<SwaggerInfoPo>> queryPage(SwaggerInfoQueryDTO swaggerInfoQueryDTO) {
        swaggerInfoQueryDTO.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        return swaggerInfoService.queryPage(swaggerInfoQueryDTO);
    }

    @PostMapping("/add/swagger.api")
    @ApiOperation("swaggerApi确认导入")
    @ApiImplicitParam(name = "swaggerInfoId", value = "swagger id", required = true)
    @AuditLog
    public ResultDTO<String> addApiFromSwagger(@RequestBody @Valid SwaggerApiDTO swaggerApiDTO) {
        IpaasUserInfo userInfo = IpaasUserContext.getUserInfo();
        swaggerApiDTO.setInCharge(userInfo.getUserIdentification());
        return swaggerApiService.addApiFromSwagger(swaggerApiDTO);
    }

    @PostMapping("/api/update")
    @ApiOperation("更新Swagger API")
    @ApiImplicitParam(name = "swaggerInfoId", value = "swagger id", required = true)
    @AuditLog
    public ResultDTO<SwaggerInfoPo> updateApi(@NotNull(message = "swaggerInfoId不能为空") Integer swaggerInfoId) {
        try {
            swaggerInfoService.updateSwaggerApi(swaggerInfoId, IpaasUserContext.getUserInfo().getUserIdentification());
        } catch (Exception e) {
            ResultDTO<Object> resultDTO = new ResultDTO<>();
            resultDTO.setSuccess(false);
            resultDTO.setErrorMessage("更新Api失败：" + e.getMessage());
            log.error("更新Swagger Api失败，swaggerInfoId：{}，error：{}", swaggerInfoId, e);
        }
        return show(swaggerInfoId);
    }


}
