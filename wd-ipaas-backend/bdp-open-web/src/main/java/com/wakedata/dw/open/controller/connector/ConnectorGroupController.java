package com.wakedata.dw.open.controller.connector;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.service.connector.ConnectorGroupService;
import com.wakedata.dw.open.service.connector.dto.ConnectorGroupDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/17 10:17
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/group")
@Api(value = "连接器平台分类管理", tags = "连接器平台分类管理")
@Validated
public class ConnectorGroupController {

    @Resource
    private ConnectorGroupService connectorGroupService;

    @PostMapping("/add")
    @ApiOperation(value = "新增平台分类", notes = "新增平台分类", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorGroupDTO connectorGroupDTO) {
        return connectorGroupService.addOfModifyConnectorGroup(connectorGroupDTO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "更新平台分类", notes = "更新平台分类", httpMethod = "POST")
    public ResultDTO<Long> modify(@Validated @RequestBody ConnectorGroupDTO connectorGroupDTO) {
        return connectorGroupService.addOfModifyConnectorGroup(connectorGroupDTO);
    }

    // 需求改动，暂时不支持拖拉改动平台分类排序
    @PostMapping("/update/list")
    @ApiOperation(value = "批量更新平台分类", notes = "批量更新平台分类（用于拖拉改动分类排序）", httpMethod = "POST")
    public ResultDTO<Long> modifyByList(@RequestBody List<ConnectorGroupDTO> connectorGroupDTOList) {
        return null;
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除平台分类", notes = "删除平台分类", httpMethod = "POST")
    public ResultDTO<Boolean> delete(@RequestParam(name = "id") Long id) {
        return connectorGroupService.deleteConnectorGroup(id);
    }

    @PostMapping("/query")
    @ApiOperation(value = "查询平台分类", notes = "id不为空时只返回单个查询，支持查询指定parentId下的平台分类树（但不包括id等于parentId的分类）", httpMethod = "POST")
    public ResultDTO<List<ConnectorGroupDTO>> query(@RequestBody ConnectorGroupDTO connectorGroupDTO) {
        return connectorGroupService.queryConnectorGroup(connectorGroupDTO);
    }

}
