package com.wakedata.dw.open.controller.connector;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.ConnectorApiAuthTypeEnum;
import com.wakedata.dw.open.model.query.ConnectorPageQuery;
import com.wakedata.dw.open.service.connector.ConnectorParamsService;
import com.wakedata.dw.open.service.connector.ConnectorService;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorParamsDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/18 10:05
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector")
@Api(value = "连接器平台管理", tags = "连接器平台管理")
@Validated
public class ConnectorController {

    @Resource
    private ConnectorService connectorService;

    @Resource
    private ConnectorParamsService connectorParamsService;

    @PostMapping("/add")
    @ApiOperation(value = "新增连接器平台", notes = "新增连接器平台", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorDTO connectorDTO) {
        return connectorService.create(connectorDTO);
    }

    @PostMapping("/modify")
    @ApiOperation(value = "更新连接器平台", notes = "更新连接器平台", httpMethod = "POST")
    public ResultDTO<Boolean> modify(@Validated @RequestBody ConnectorDTO connectorDTO) {
        return connectorService.modify(connectorDTO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除连接器平台", notes = "删除连接器平台", httpMethod = "POST")
    public ResultDTO<Boolean> delete(@RequestParam(value = "id", required = true) Long id) {
        return connectorService.delete(id);
    }

    @PostMapping("/query")
    @ApiOperation(value = "查询连接器平台", notes = "更新连接器平台", httpMethod = "POST")
    public PageResultDTO<List<ConnectorDTO>> query(@RequestBody ConnectorPageQuery connectorPageQuery) {
        return connectorService.query(connectorPageQuery);
    }

    @GetMapping("/query.name")
    @ApiOperation(value = "查询连接器平台的id和name", notes = "查询连接器平台的id和name", httpMethod = "GET")
    public ResultDTO<List<ConnectorDTO>> queryIdAndName() {
        return connectorService.queryIdAndName();
    }

    @GetMapping("/findParamsByConnectorId")
    @ApiOperation(value = "根据平台id查询平台鉴权字段", notes = "根据平台id查询平台鉴权字段", httpMethod = "GET")
    public ResultDTO<List<ConnectorParamsDTO>> findParamsByConnectorId(@RequestParam("connectorId") Long connectorId) {
        return connectorParamsService.findParamsByConnectorId(connectorId);
    }

    @GetMapping("/query.auth.type")
    @ApiOperation(value = "获取平台鉴权类型", notes = "获取平台鉴权类型", httpMethod = "GET")
    public ResultDTO<Map<String,ConnectorApiAuthTypeEnum>> queryAuthType() {
        return connectorParamsService.queryAuthType();
    }
}
