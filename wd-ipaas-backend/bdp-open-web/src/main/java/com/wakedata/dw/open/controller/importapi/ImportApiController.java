package com.wakedata.dw.open.controller.importapi;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.exportapi.dto.openapi.ImportOpenApiResultDTO;
import com.wakedata.dw.open.service.importapi.ImportApiService;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wujunqiang
 * @since 2023/2/3 10:42
 */
@Api(value = "导入API相关接口", tags = "导入API相关接口")
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/import")
public class ImportApiController {

    @Autowired
    private ImportApiService importApiService;

    @ApiOperation(value = "通过文件方式导入开放平台API", notes = "通过文件方式导入开放平台API", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tempFilePath", value = "已经上传的导入文件路径", paramType = "query"),
            @ApiImplicitParam(name = "apiGroupId", value = "接口分类id", paramType = "query")
    })
    @PostMapping("/open.api.from.file")
    public ResultDTO<ImportOpenApiResultDTO> importOpenApiFromFile(@RequestParam("tempFilePath") String tempFilePath, @RequestParam("apiGroupId") Integer apiGroupId) {
        return ResultDTO.success(importApiService.importOpenApiFromFile(tempFilePath, apiGroupId, IpaasUserContext.getUserInfo()));
    }

    @ApiOperation(value = "通过文件方式导入连接器", notes = "通过文件方式导入连接器", httpMethod = "POST")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tempFilePath", value = "已经上传的导入文件路径", paramType = "query"),
            @ApiImplicitParam(name = "connectorGroupId", value = "连接器分类id", paramType = "query")
    })
    @PostMapping("/connector.from.file")
    public ResultDTO<ImportOpenApiResultDTO> importConnectorFromFile(@RequestParam("tempFilePath") String tempFilePath, @RequestParam("connectorGroupId") Long connectorGroupId) {
        return ResultDTO.success(importApiService.importConnectorFromFile(tempFilePath, connectorGroupId, IpaasUserContext.getUserInfo()));
    }

}
