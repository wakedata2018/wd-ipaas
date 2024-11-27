package com.wakedata.dw.open.controller.api;

import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.datasource.model.DatasourceTableColumnDo;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.DataTypeEnum;
import com.wakedata.dw.open.enums.PublicReturnParametersEnum;
import com.wakedata.dw.open.service.api.RespParamMappingRuleService;
import com.wakedata.dw.open.service.api.dto.RespParamMappingRuleDTO;
import com.wakedata.dw.open.model.query.RespParamMappingRuleQuery;
import com.wakedata.dw.open.service.impl.utils.ParamBuildUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * api响应体参数映射规则controller
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 10:05
 */
@RestController
@RequestMapping(value = "${spring.mvc.backend.prefix}/business/api/resp/param/mapping")
@Api(value = "api响应体参数映射规则controller", tags = "api响应体参数映射规则controller")
@Validated
public class RespParamMappingRuleController {

    @Resource
    private RespParamMappingRuleService respParamMappingRuleService;

    @AuditLog
    @ApiOperation(value = "新增映射规则", notes = "新增映射规则", httpMethod = "POST")
    @PostMapping(value = "/rule/add")
    public ResultDTO<Integer> addRule(@RequestBody @Validated RespParamMappingRuleDTO respParamMappingRuleDTO) {
        return respParamMappingRuleService.addRule(respParamMappingRuleDTO);
    }

    @AuditLog
    @ApiOperation(value = "更新映射规则", notes = "更新映射规则", httpMethod = "POST")
    @PostMapping(value = "/rule/update")
    public ResultDTO<Integer> updateRule(@RequestBody @Validated RespParamMappingRuleDTO respParamMappingRuleDTO) {
        return respParamMappingRuleService.updateRule(respParamMappingRuleDTO);
    }

    @AuditLog
    @ApiOperation(value = "删除映射规则", notes = "删除映射规则", httpMethod = "POST")
    @PostMapping(value = "/rule/delete")
    public ResultDTO<Integer> deleteRule(@RequestParam("id") Integer id) {
        return respParamMappingRuleService.deleteRule(id);
    }

    @AuditLog
    @ApiOperation(value = "查询映射规则", notes = "查询映射规则", httpMethod = "POST")
    @PostMapping(value = "/rule/query")
    public PageResultDTO<List<RespParamMappingRuleDTO>> queryRule(@RequestBody RespParamMappingRuleQuery respParamMappingRuleQuery) {
        return respParamMappingRuleService.queryRule(respParamMappingRuleQuery);
    }

    @AuditLog
    @ApiOperation(value = "根据id查询映射规则", notes = "根据id查询映射规则", httpMethod = "POST")
    @PostMapping(value = "/rule/query.by.id")
    public ResultDTO<RespParamMappingRuleDTO> queryRuleById(@RequestParam("id") Integer id) {
        return respParamMappingRuleService.queryRuleById(id);
    }

    @AuditLog
    @ApiOperation(value = "查询所有映射规则id和名称", notes = "查询所有映射规则id和名称", httpMethod = "GET")
    @GetMapping(value = "/rule/query.id.name")
    public ResultDTO<List<RespParamMappingRuleDTO>> queryRuleIdAndName() {
        return respParamMappingRuleService.queryRuleIdAndName();
    }

    @ApiOperation(value = "获取集成云返回体格式", notes = "获取集成云返回体格式", httpMethod = "GET")
    @GetMapping(value = "/rule/get.resp.base.param")
    public ResultDTO<List<DatasourceTableColumnDo>> getRespBaseParam() {
        List<DatasourceTableColumnDo> datasourceTableColumnDoList = ParamBuildUtil.getDefaultPageResultParam(DataAssetEnums.DataApiOperationType.QUERY);
        datasourceTableColumnDoList.forEach( n-> {
            if (PublicReturnParametersEnum.DATA.getAttributeName().equals(n.getDatasourceTableColumnName())) {
                n.setDatasourceTableColumnType(DataTypeEnum.OBJECT.getType());
            }
        });
        return ResultDTO.success(datasourceTableColumnDoList);
    }

}
