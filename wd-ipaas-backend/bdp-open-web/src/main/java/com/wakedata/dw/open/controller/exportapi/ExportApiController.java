package com.wakedata.dw.open.controller.exportapi;

import com.wakedata.dw.open.service.exportapi.ExportApiService;
import com.wakedata.dw.open.service.exportapi.dto.connector.ExportConnectorParamDTO;
import com.wakedata.dw.open.service.exportapi.dto.openapi.ExportOpenApiParamDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * 导出相关接口
 *
 * @author wujunqiang
 * @since 2023/1/12 11:29
 */
@Api(value = "导出API相关接口", tags = "导出API相关接口")
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/export")
public class ExportApiController {

    @Autowired
    private ExportApiService exportApiService;

    @ApiOperation(value = "将连接器导出成json文件", notes = "将连接器导出成json文件", httpMethod = "POST")
    @PostMapping("/connector.to.file")
    public void exportConnectorApiToFile(@RequestBody @Validated ExportConnectorParamDTO dto, HttpServletResponse response) {
        exportApiService.exportConnectorApiToFile(dto, response);
    }

    @ApiOperation(value = "将开放平台API导出成json文件", notes = "将开放平台API导出成json文件", httpMethod = "POST")
    @PostMapping("/open.api.to.file")
    public void exportOpenApiToFile(@RequestBody @Validated ExportOpenApiParamDTO dto, HttpServletResponse response) {
        exportApiService.exportOpenApiToFile(dto, response);
    }

    @ApiOperation(value = "将开放平台API导出成markdown文件", notes = "将开放平台API导出成markdown文件", httpMethod = "POST")
    @PostMapping("/open.api.to.markdown")
    public void exportOpenApiToMarkdown(@RequestBody ExportOpenApiParamDTO dto, HttpServletResponse response) {
        exportApiService.exportOpenApiToMarkdown(dto, response);
    }

}
