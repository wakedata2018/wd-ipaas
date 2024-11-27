package com.wakedata.dw.open.controller.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.connector.ConnectorApiGroupService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiGroupRelevanceDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 17:06
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/api/group")
@Api(value = "连接器平台api分组管理", tags = "连接器平台api分组管理")
@Validated
public class ConnectorApiGroupController {

    @Resource
    private ConnectorApiGroupService connectorApiGroupService;

    @PostMapping("/add")
    @ApiOperation(value = "新增api分组", notes = "新增api分组", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorApiGroupDTO connectorApiDTO) {
        return connectorApiGroupService.createOfModify(connectorApiDTO);
    }

    @PostMapping("/modify")
    @ApiOperation(value = "更新api分组", notes = "更新api分组", httpMethod = "POST")
    public ResultDTO<Long> modify(@Validated @RequestBody ConnectorApiGroupDTO connectorApiDTO) {
        return connectorApiGroupService.createOfModify(connectorApiDTO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除api分组", notes = "删除api分组", httpMethod = "POST")
    public ResultDTO<Boolean> delete(@RequestParam(value = "id") Long id) {
        return connectorApiGroupService.delete(id);
    }

    @PostMapping("/queryById")
    @ApiOperation(value = "查询单个api分组（包含其下api列表）", notes = "查询单个api分组（包含其下api列表）", httpMethod = "POST")
    public ResultDTO<ConnectorApiGroupDTO> queryById(@RequestParam("id") Long id) {
        return connectorApiGroupService.queryById(id);
    }

    @GetMapping("/query.by.connector.id")
    @ApiOperation(value = "根据平台id，查询api分组列表（平台id为空时，就是查询全平台下的api分组列表）", notes = "根据平台id，查询api分组列表（平台id为空时，就是查询全平台下的api分组列表）", httpMethod = "GET")
    public ResultDTO<List<ConnectorApiGroupRelevanceDTO>> queryByConnectorId(@RequestParam(value = "connectorId", required = false) Long connectorId) {
        return connectorApiGroupService.queryByConnectorId(connectorId);
    }
}
