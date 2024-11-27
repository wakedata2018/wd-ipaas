package com.wakedata.dw.open.controller.connector;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.ConnectorSecretKeyPageQuery;
import com.wakedata.dw.open.service.connector.ConnectorSecretKeyService;
import com.wakedata.dw.open.service.connector.dto.ConnectorSecretKeyDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 平台密钥接口
 *
 * @author wujunqiang
 * @since 2022/11/21 10:18
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/connector/secret")
@Api(value = "平台密钥接口", tags = "平台密钥接口")
public class ConnectorSecretKeyController {

    @Resource
    private ConnectorSecretKeyService connectorSecretKeyService;

    @GetMapping("/findByConnectorId")
    @ApiOperation(value = "根据平台id查询平台密钥", notes = "根据平台id查询平台密钥（只返回启用状态的密钥数据）", httpMethod = "GET")
    public ResultDTO<List<ConnectorSecretKeyDTO>> findByConnectorId(@RequestParam("connectorId") Long connectorId) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        return connectorSecretKeyService.findByConnectorId(ipaasUserInfo.getLesseeId(), connectorId);
    }

    @PostMapping("/add")
    @ApiOperation(value = "新增平台密钥", notes = "新增平台密钥", httpMethod = "POST")
    public ResultDTO<Long> create(@Validated @RequestBody ConnectorSecretKeyDTO connectorSecretKeyDTO) {
        return connectorSecretKeyService.create(connectorSecretKeyDTO);
    }

    @PostMapping("/update")
    @ApiOperation(value = "修改平台密钥", notes = "修改平台密钥", httpMethod = "POST")
    public ResultDTO<Boolean> update(@Validated @RequestBody ConnectorSecretKeyDTO connectorSecretKeyDTO) {
        return connectorSecretKeyService.update(connectorSecretKeyDTO);
    }

    @GetMapping("/deleteById")
    @ApiOperation(value = "根据id删除平台密钥", notes = "根据id删除平台密钥", httpMethod = "GET")
    public ResultDTO<Boolean> delete(@RequestParam("id") Long id) {
        return connectorSecretKeyService.delete(id);
    }

    @PostMapping("/page")
    @ApiOperation(value = "分页查询平台密钥", notes = "分页查询平台密钥", httpMethod = "POST")
    public PageResultDTO<List<ConnectorSecretKeyDTO>> page(@RequestBody ConnectorSecretKeyPageQuery pageQuery) {
        return connectorSecretKeyService.page(pageQuery);
    }

}
