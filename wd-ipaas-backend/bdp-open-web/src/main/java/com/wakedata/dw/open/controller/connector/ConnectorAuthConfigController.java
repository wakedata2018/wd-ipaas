package com.wakedata.dw.open.controller.connector;

import com.wakedata.common.core.constants.ActiveStatusEnum;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.ConnectorAuthCofigPageQuery;
import com.wakedata.dw.open.model.query.ConnectorPageQuery;
import com.wakedata.dw.open.service.connector.ConnectorAuthConfigService;
import com.wakedata.dw.open.service.connector.dto.ConnectorAuthConfigDTO;
import com.wakedata.dw.open.service.connector.dto.ConnectorDTO;
import com.wakedata.dw.open.utils.UUIDUtils;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 连接器鉴权配置
 * @author luomeng
 * @date 2022/11/24 11:21
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/auth.config")
@Api(value = "连接器鉴权配置管理", tags = "连接器鉴权配置管理")
@Validated
public class ConnectorAuthConfigController {

    @Resource
    private ConnectorAuthConfigService connectorAuthConfigService;

    @PostMapping("/add")
    @ApiOperation(value = "新增连接器鉴权配置", notes = "新增连接器鉴权配置", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorAuthConfigDTO authConfigDTO) {
        authConfigDTO.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        authConfigDTO.setCreateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        authConfigDTO.setUpdateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        authConfigDTO.setAuthIdentification(UUIDUtils.generateSimpleUUID());
        return connectorAuthConfigService.create(authConfigDTO);
    }

    @PostMapping("/modify")
    @ApiOperation(value = "更新连接器鉴权配置", notes = "更新连接器平台", httpMethod = "POST")
    public ResultDTO<Boolean> modify(@Validated @RequestBody ConnectorAuthConfigDTO authConfigDTO) {
        authConfigDTO.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        authConfigDTO.setUpdateBy(IpaasUserContext.getUserInfo().getUserIdentification());
        authConfigDTO.setStatus(ActiveStatusEnum.ACTIVE.getValue());
        return connectorAuthConfigService.modify(authConfigDTO);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除连接器鉴权配置", notes = "删除连接器鉴权配置", httpMethod = "POST")
    public ResultDTO<Boolean> delete(@RequestParam(value = "id", required = true) Long id) {
        return connectorAuthConfigService.deleteAuthConfig(IpaasUserContext.getUserInfo().getLesseeId(),id);
    }

    @PostMapping("/query")
    @ApiOperation(value = "查询连接器鉴权配置", notes = "查询连接器鉴权配置", httpMethod = "POST")
    public PageResultDTO<List<ConnectorAuthConfigDTO>> query(@RequestBody ConnectorAuthCofigPageQuery connectorPageQuery) {
        connectorPageQuery.setLesseeId(IpaasUserContext.getUserInfo().getLesseeId());
        return connectorAuthConfigService.query(connectorPageQuery);
    }

    @GetMapping("/queryById")
    @ApiOperation(value = "根据配置id查询连接器鉴权配置", notes = "根据配置id查询连接器鉴权配置", httpMethod = "GET")
    @ApiImplicitParams({@ApiImplicitParam(name = "id",value = "鉴权配置id",paramType = "query")})
    public ResultDTO<ConnectorAuthConfigDTO> queryById(Long id) {
        return ResultDTO.success(connectorAuthConfigService.getConnectorAuthConfigById(id));
    }


    @GetMapping("/getAllAuthConfig")
    @ApiOperation(value = "获取租户下所有的连接器鉴权配置")
    public ResultDTO<List<ConnectorAuthConfigDTO>> getAllAuthConfig(){
        return ResultDTO.success(connectorAuthConfigService.getAllAuthConfig(IpaasUserContext.getUserInfo().getLesseeId()));

    }
}
