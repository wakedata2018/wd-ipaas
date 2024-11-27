package com.wakedata.dw.open.controller.connector;

import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.ConnectorApiPageQuery;
import com.wakedata.dw.open.service.connector.ConnectorApiService;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorApiDetailDTO;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/11/21 14:52
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/api")
@Api(value = "连接器平台api管理", tags = "连接器平台api管理")
@Validated
public class ConnectorApiController {

    @Resource
    private ConnectorApiService connectorApiService;

    @PostMapping("/add")
    @ApiOperation(value = "新增连接器平台api", notes = "新增连接器平台api", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorApiDetailDTO connectorApiDetailDTO) {
        Date now = new Date();
        connectorApiDetailDTO.getConnectorApi().setCreateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        connectorApiDetailDTO.getConnectorApi().setUpdateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        connectorApiDetailDTO.getConnectorApi().setCreateTime(now);
        connectorApiDetailDTO.getConnectorApi().setUpdateTime(now);
        connectorApiDetailDTO.getConnectorApi().setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        return connectorApiService.create(connectorApiDetailDTO);
    }

    @PostMapping("/modify")
    @ApiOperation(value = "更新连接器平台api", notes = "更新连接器平台api", httpMethod = "POST")
    public ResultDTO<Long> modify(@Validated @RequestBody ConnectorApiDetailDTO connectorApiDetailDTO) {
        connectorApiDetailDTO.getConnectorApi().setUpdateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        connectorApiDetailDTO.getConnectorApi().setUpdateTime(new Date());
        connectorApiDetailDTO.getConnectorApi().setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        return connectorApiService.modify(connectorApiDetailDTO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除连接器平台api", notes = "删除连接器平台api", httpMethod = "POST")
    public ResultDTO<Boolean> delete(@RequestParam(value = "id") Long id) {
        return connectorApiService.delete(id);
    }

    @GetMapping("/detail")
    @ApiOperation(value = "查询连接器API详情", notes = "查询连接器API详情", httpMethod = "GET")
    public ResultDTO<ConnectorApiDetailDTO> detail(@RequestParam("id") Long id) {
        return connectorApiService.detail(id);
    }

    @PostMapping("/query")
    @ApiOperation(value = "分页查询连接器平台api", notes = "分页查询连接器平台api", httpMethod = "POST")
    public PageResultDTO<List<ConnectorApiDTO>> query(@RequestBody ConnectorApiPageQuery connectorApiPageQuery) {
        return connectorApiService.query(connectorApiPageQuery);
    }
}
