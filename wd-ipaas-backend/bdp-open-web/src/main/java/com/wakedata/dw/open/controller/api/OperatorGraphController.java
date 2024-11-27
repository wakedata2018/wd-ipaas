package com.wakedata.dw.open.controller.api;

import com.google.common.collect.Maps;
import com.wakedata.dw.open.datasource.enums.DatasourceTypeEnum;
import com.wakedata.dw.open.liteflow.OperatorGraphLiteflowService;
import com.wakedata.dw.open.model.api.flow.ApiFlowAttr;
import com.wakedata.dw.open.model.api.flow.Component;
import com.wakedata.dw.open.model.api.flow.operator.Operator;
import com.wakedata.dw.open.model.api.flow.operator.OperatorAttribute;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.service.api.dto.LiteFlowAllOperatorTemplateDTO;
import com.wakedata.dw.open.service.api.flow.OperatorGraphService;
import com.wakedata.dw.open.dto.ResultDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

import static com.wakedata.dw.open.model.api.flow.Component.Kind.api_custom_sql;
import static com.wakedata.dw.open.model.api.flow.Component.Kind.api_normal_table;

/**
 * 拖拽组件Controller
 *
 * @author ZhangXueJun
 * @title LiteFlowController
 * @date 2021/5/11 14:07
 * @projectName dw-open
 * @description
 */
@Slf4j
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/api/component")
@Validated
@Api(value = "算子管理", tags = "算子管理")
public class OperatorGraphController {

    @Autowired
    private OperatorGraphService operatorGraphService;

    @Resource
    private OperatorGraphLiteflowService operatorGraphLiteflowService;

    /**
     * 保存任务组件详细信息:涵盖组件节点信息，边信息
     *
     * @param
     * @return
     */
    @RequestMapping("/operator/list/impl")
    public ResultDTO<List<OperatorAttribute>> listOperatorInterfaceImpl() {
        List<OperatorAttribute> operatorAttributes = operatorGraphService.listOperatorAttributes();
        operatorAttributes.sort(Comparator.comparing(OperatorAttribute::getOrder));

        List<DataSourcePo> apiComponents = operatorGraphService.apiComponents();

        Map<DatasourceTypeEnum, List<DataSourcePo>> groupByDataSources =
                apiComponents.stream().collect(Collectors.groupingBy(DataSourcePo::getDbType, Collectors.toList()));

        operatorAttributes.stream().map(operatorAttribute -> {
            try {
                Constructor<Operator> constructor =
                        (Constructor<Operator>) Class.forName(operatorAttribute.getClassName()).getConstructor();
                Operator operator = constructor.newInstance();

                /**
                 * 组装边信息给前端
                 */
                operatorAttribute = operatorGraphLiteflowService.assembleEdgeAttribute(operatorAttribute, operator);

                if (StringUtils.isEmpty(operatorAttribute.getUniqueName())) {

                    return operator;
                }

                Component.Kind componentKind = null;
                try {
                    if (operatorAttribute.getUniqueName().startsWith(Component.Kind.api.name())) {
                        componentKind = Component.Kind.api;
                    } else if (operatorAttribute.getUniqueName().startsWith(Component.Kind.transform.name())) {
                        componentKind = Component.Kind.transform;
                    } else {
                        componentKind = Component.Kind.valueOf(operatorAttribute.getUniqueName());
                    }
                } catch (IllegalArgumentException e) {
                    log.warn(e.getMessage(), e);
                }
                switch (componentKind) {
                    case api:
                        if (operatorAttribute.getUniqueName().equals(api_normal_table.name())) {
                            operatorAttribute.setComponents(groupByDataSources);
                        } else if (operatorAttribute.getUniqueName().equals(api_custom_sql.name())) {
                            Map<DatasourceTypeEnum, List<DataSourcePo>> excludeHbaseDataSources
                                    = Maps.newLinkedHashMap(groupByDataSources);
                            excludeHbaseDataSources.remove(DatasourceTypeEnum.HBASE);
                            operatorAttribute.setComponents(excludeHbaseDataSources);
                        }
                        break;
                    case transform:
                        break;
                    default:
                        break;
                }
                return operator;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        return ResultDTO.success(operatorAttributes);
    }

    /**
     * 获取当前算子的入边信息
     *
     * @param apiFlowAttr ApiFlowAttr
     * @param operatorId  算子id
     * @return 当前算子的上级算子节点集合
     */
    @RequestMapping("/operator/inputEdges")
    public ResultDTO<List<Operator>> listOperatorInputEdges(@RequestBody ApiFlowAttr apiFlowAttr, String operatorId) {
        List<Operator> operators = operatorGraphLiteflowService.getOperatorInputEdges(apiFlowAttr,operatorId);
        return ResultDTO.success(new ArrayList<>(operators));
    }

    /**
     * 获取算子的参数模板信息
     *
     * @param apiFlowAttr  流程编排图信息
     * @param operatorType 指定算子
     * @return 算子参数列表模板信息
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "operatorType", value = "类型"),
            @ApiImplicitParam(name = "isForBreak",value = "是否为循环算子退出条件，退出需要支持可选当前循环算子的循环变量",paramType = "query")
    })
    @RequestMapping("/operator/listAllOperatorTemplateDTOs")
    public ResultDTO<List<LiteFlowAllOperatorTemplateDTO>> getLiteFlowAllOperatorTemplateDTOList(@RequestBody ApiFlowAttr apiFlowAttr, String operatorType
            ,@RequestParam(name = "isForBreak",defaultValue = "false",required = false) Boolean isForBreak) {

        List<LiteFlowAllOperatorTemplateDTO> liteFlowAllOperatorTemplateDTOList = operatorGraphService.getLiteFlowAllOperatorTemplateDTOList(apiFlowAttr, operatorType,isForBreak);
        return ResultDTO.success(liteFlowAllOperatorTemplateDTOList);
    }

    /**
     * 获取判断算子的运算符列表
     *
     * @return List<String> 返回已启用的运算符（在枚举中配置）
     */
    @GetMapping("/operator/query/operators")
    @ApiOperation(value = "获取判断算子的运算符列表", notes = "获取判断算子的运算符列表,返回已启用的运算符（在枚举中配置）")
    public ResultDTO<Map<String, String>> queryAllJudgeOperatorList() {
        return ResultDTO.success(operatorGraphService.queryAllJudgeOperatorList());
    }


}
