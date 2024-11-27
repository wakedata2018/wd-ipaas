package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.enums.PublicReturnParametersEnum;
import com.wakedata.dw.open.enums.StatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.api.DataAssetApiMapper;
import com.wakedata.dw.open.mapper.api.RespParamMappingRuleMapper;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.RespParamMappingRulePo;
import com.wakedata.dw.open.model.query.RespParamMappingRuleQuery;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.RespParamMappingRuleService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.RespParamMappingRuleDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.utils.JsonSchemaConvertUtil;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import com.wakedata.dw.open.utils.threadlocal.IpaasUserContext;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 响应体映规则service
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/3/3 10:26
 */
@Service
public class RespParamMappingRuleServiceImpl extends BaseServiceImpl<RespParamMappingRulePo, RespParamMappingRuleMapper> implements RespParamMappingRuleService {

    @Resource
    private RespParamMappingRuleMapper respParamMappingRuleMapper;

    @Resource
    private DataAssetApiMapper dataAssetApiMapper;

    @Resource
    private ApiResponseParamServiceImpl apiResponseParamService;

    /**
     * 根节点的引用值表达式
     */
    private static final String DOLLAR = "$";
    /**
     * 圆点分隔符常量
     */
    private static final String DOT = ".";
    /**
     * 冒号分隔符常量
     */
    private static final String COLON = ":";

    @Override
    protected void init(CurdService<RespParamMappingRulePo> curdService, RespParamMappingRuleMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public ResultDTO<Integer> addRule(RespParamMappingRuleDTO respParamMappingRuleDTO) {
        // 参数校验
        checkRespParamMappingRuleExists(respParamMappingRuleDTO);
        RespParamMappingRulePo respParamMappingRulePo = convertToRespParamMappingRulePo(respParamMappingRuleDTO, true);
        respParamMappingRuleMapper.insertSelective(respParamMappingRulePo);
        return ResultDTO.success(respParamMappingRulePo.getId());
    }

    @Override
    public ResultDTO<Integer> updateRule(RespParamMappingRuleDTO respParamMappingRuleDTO) {
        // 参数校验
        checkRespParamMappingRuleExists(respParamMappingRuleDTO);
        RespParamMappingRulePo respParamMappingRulePo = convertToRespParamMappingRulePo(respParamMappingRuleDTO, false);
        return ResultDTO.success(respParamMappingRuleMapper.updateByPrimaryKeySelective(respParamMappingRulePo));
    }

    @Override
    public ResultDTO<Integer> deleteRule(Integer id) {
        // 校验该映射规则是否是默认规则并且是否已经被api使用
        checkMappingRuleBeUsed(id);

        return ResultDTO.success(respParamMappingRuleMapper.deleteByPrimaryKey(id));
    }

    @Override
    public PageResultDTO<List<RespParamMappingRuleDTO>> queryRule(RespParamMappingRuleQuery respParamMappingRuleQuery) {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        PageHelper.startPage(respParamMappingRuleQuery.getPageNo(), respParamMappingRuleQuery.getPageSize());
        // 查询出是不含默认规则的参数映射规则列表
        List<RespParamMappingRulePo> respParamMappingRulePos = respParamMappingRuleMapper.query(respParamMappingRuleQuery, ipaasUserInfo.getLesseeId().intValue());
        PageInfo<RespParamMappingRulePo> pageInfo = new PageInfo<>(respParamMappingRulePos);
        // 对象类型转换
        List<RespParamMappingRuleDTO> respParamMappingRuleDTOList = BeanUtil.copyList(pageInfo.getList(), RespParamMappingRuleDTO.class);

        // 拼接关联的api数量
        if (CollectionUtil.isNotEmpty(respParamMappingRuleDTOList)) {
            List<Integer> respParamMappingRuleIds = respParamMappingRuleDTOList.stream().map(RespParamMappingRuleDTO::getId).collect(Collectors.toList());
            List<DataAssetApiPo> dataAssetApiPoList = dataAssetApiMapper.listByRespMappingRule(respParamMappingRuleIds);
            Map<Integer, List<DataAssetApiPo>> apiMap = dataAssetApiPoList.stream().collect(Collectors.groupingBy(DataAssetApiPo::getRespMappingRule));
            if (CollectionUtil.isNotEmpty(dataAssetApiPoList)) {
                respParamMappingRuleDTOList.forEach(r -> {
                    if (CollectionUtil.isNotEmpty(apiMap.get(r.getId()))) {
                        r.setApiCount(apiMap.get(r.getId()).size());
                    }else {
                        r.setApiCount(0);
                    }
                });
            }
        }
        PageResultDTO<List<RespParamMappingRuleDTO>> resultDTO = new PageResultDTO<>();
        resultDTO.setData(respParamMappingRuleDTOList);
        resultDTO.setPageNo(respParamMappingRuleQuery.getPageNo());
        resultDTO.setPageSize(respParamMappingRuleQuery.getPageSize());
        resultDTO.setTotalCount((int)pageInfo.getTotal());
        return resultDTO;
    }

    @Override
    public ResultDTO<RespParamMappingRuleDTO> queryRuleById(Integer id) {
        RespParamMappingRulePo respParamMappingRulePo = respParamMappingRuleMapper.selectByPrimaryKey(id);
        RespParamMappingRuleDTO respParamMappingRuleDTO = BeanUtil.copy(respParamMappingRulePo, RespParamMappingRuleDTO.class);
        // 获取关联的api数量
        Example example = new Example(DataAssetApiPo.class);
        example.createCriteria().andEqualTo("respMappingRule", id);
        respParamMappingRuleDTO.setApiCount(dataAssetApiMapper.selectCountByExample(example));
        return ResultDTO.success(respParamMappingRuleDTO);
    }

    @Override
    public ResultDTO<List<RespParamMappingRuleDTO>> queryRuleIdAndName() {
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        // 查询得到已启用的非默认映射规则
        Example example = new Example(RespParamMappingRulePo.class);
        example.createCriteria()
                .andEqualTo("status", StatusEnum.ACTIVE.getCode())
                .andEqualTo("lesseeId", ipaasUserInfo.getLesseeId().intValue())
                .andEqualTo("isDefaultRule", Boolean.FALSE);
        List<RespParamMappingRulePo> respParamMappingRulePos = respParamMappingRuleMapper.selectByExample(example);
        // 查询出已启用的默认映射规则（不带租户隔离）
        example.clear();
        example.createCriteria()
                .andEqualTo("isDefaultRule", Boolean.TRUE)
                .andEqualTo("status", StatusEnum.ACTIVE.getCode());
        List<RespParamMappingRulePo> respParamMappingRulePosDefault = respParamMappingRuleMapper.selectByExample(example);
        respParamMappingRulePos.addAll(respParamMappingRulePosDefault);

        List<RespParamMappingRuleDTO> respParamMappingRuleDTOList = respParamMappingRulePos.stream().map(r -> new RespParamMappingRuleDTO(r.getId(), r.getRespParamMappingRuleName())).collect(Collectors.toList());
        return ResultDTO.success(respParamMappingRuleDTOList);
    }

    /**
     * 校验响应体参数映射规则
     */
    private void checkRespParamMappingRuleExists(RespParamMappingRuleDTO respParamMappingRuleDTO){
        IpaasUserInfo ipaasUserInfo = new IpaasUserInfo();
        // 状态字典数据是否合规校验
        if(respParamMappingRuleDTO.getStatus() > StatusEnum.ACTIVE.getCode()) {
            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_status_error);
        }
        // 映射规则重名校验
        Example example = new Example(RespParamMappingRulePo.class);
        example.createCriteria()
                .andEqualTo("lesseeId", ipaasUserInfo.getLesseeId())
                .andEqualTo("respParamMappingRuleName", respParamMappingRuleDTO.getRespParamMappingRuleName())
                .andNotEqualTo("id", respParamMappingRuleDTO.getId());
        if (respParamMappingRuleMapper.selectCountByExample(example) > 0) {
            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_name_must_not_be_repeated);
        }
        // 通过id判断是否是默认的映射规则，不允许更改默认映射规则（查询也不返回默认的映射规则信息）
        if (ObjectUtil.isNotEmpty(respParamMappingRuleDTO.getId())) {
            checkMappingRuleBeUsed(respParamMappingRuleDTO.getId());
            RespParamMappingRulePo respParamMappingRulePo = respParamMappingRuleMapper.selectByPrimaryKey(respParamMappingRuleDTO.getId());
            if (ObjectUtil.isNotEmpty(respParamMappingRulePo) && respParamMappingRulePo.getIsDefaultRule()) {
                throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_is_default_rule_can_not_to_update_or_delete);
            }
        }

    }

    /**
     * 校验响应体映射规则是否已经被api使用
     * @param id 映射规则id
     */
    private void checkMappingRuleBeUsed(Integer id) {
        // 判断是否默认映射规则，默认映射规则不允许修改和删除
        Example example = new Example(RespParamMappingRulePo.class);
        example.createCriteria().andEqualTo("id", id);
        RespParamMappingRulePo respParamMappingRulePo = respParamMappingRuleMapper.selectByPrimaryKey(id);
        if (ObjectUtil.isNotEmpty(respParamMappingRulePo) && respParamMappingRulePo.getIsDefaultRule()) {
            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_is_default_rule_can_not_to_update_or_delete);
        }
        // 判断已经被api使用的映射规则不能被修改
        Example exampleApi = new Example(DataAssetApiPo.class);
        exampleApi.createCriteria().andEqualTo("respMappingRule", id);
        List<DataAssetApiPo> dataAssetApiPoList = dataAssetApiMapper.selectByExample(exampleApi);
        List<String> apiName = dataAssetApiPoList.stream().map(DataAssetApiPo::getApiName).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(dataAssetApiPoList)) {
            throw new OpenException(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_already_used_by_the_api.getCode(), String.format(OpenApiMsgCodeEnum.w_resp_param_mapping_rule_already_used_by_the_api.getDesc(), apiName.toString()));
        }
    }

    private RespParamMappingRulePo convertToRespParamMappingRulePo(RespParamMappingRuleDTO respParamMappingRuleDTO, Boolean isAddRule){
        IpaasUserInfo ipaasUserInfo = IpaasUserContext.getUserInfo();
        Date date = new Date();
        RespParamMappingRulePo respParamMappingRulePo = new RespParamMappingRulePo();
        respParamMappingRulePo.setId(respParamMappingRuleDTO.getId());
        respParamMappingRulePo.setRespParamMappingRuleName(respParamMappingRuleDTO.getRespParamMappingRuleName());
        respParamMappingRulePo.setRespParamMappingRuleJsonSchema(respParamMappingRuleDTO.getRespParamMappingRuleJsonSchema());
        // 解析映射关系JSONSchema -> 解析出来了平铺的参数树 —> 从中获取表达式，生成对应的pageResultDTO（json）
        String pageResultJson = analysisJsonSchemaAndExpression(respParamMappingRuleDTO.getRespParamMappingRuleJsonSchema()).toJSONString();
        respParamMappingRulePo.setRespParamMappingRule(pageResultJson);
        respParamMappingRulePo.setDescription(respParamMappingRuleDTO.getDescription());
        respParamMappingRulePo.setStatus(respParamMappingRuleDTO.getStatus());
        // 默认参数映射规则是数据表的初始化数据，不能在页面上新增或者修改，不在DTO中提供这个字段
        respParamMappingRulePo.setIsDefaultRule(false);
        respParamMappingRulePo.setUpdateBy(ipaasUserInfo.getUserIdentification());
        respParamMappingRulePo.setUpdateTime(date);
        if (isAddRule) {
            respParamMappingRulePo.setLesseeId(ipaasUserInfo.getLesseeId());
            respParamMappingRulePo.setCreateBy(ipaasUserInfo.getUserIdentification());
            respParamMappingRulePo.setCreateTime(date);
        } else {
            respParamMappingRulePo.setLesseeId(respParamMappingRuleDTO.getLesseeId());
            respParamMappingRulePo.setCreateBy(respParamMappingRuleDTO.getCreateBy());
            respParamMappingRulePo.setCreateTime(respParamMappingRuleDTO.getCreateTime());
        }
        return respParamMappingRulePo;
    }


    /**
     * 解析JSONSchema、有映射关系的就生成表达式存储在树状结构的每一个对象中的expression字段中
     * 规则/流程 -> 调用工具方法，将JSONSchema串解析成一个树状结构ApiRespParamDTO
     *             通过迭代从上往下每一个节点对象，只要defaultValue值不为空的，说明已经进行了响应体参数规则映射
     *             在递归的时候会将父类到根节点的表达式带到每一列，子列有映射关系的，就生成表达式存放于子列对象中的expression字段里面
     *
     * @param respParamMappingRuleJsonSchema 参数映射中界面构造的参数结构
     * @return JSONObject 循环枚举生成一个pageResultDTO格式的JSONObject，在递归过程中构造好最终的映射规则后返回
     */
    private JSONObject analysisJsonSchemaAndExpression(String respParamMappingRuleJsonSchema) {
        // 此时转换出来就是完整的一整颗树（是参数信息树，也是返回参数列）
        ApiRespParamDTO apiRespParamDTO = JsonSchemaConvertUtil.convert(respParamMappingRuleJsonSchema);
        // buildExpressionDTO中含有完整的参数树，但是这里多传入一个值存放平铺处理后的参数树
        JSONObject pageResultJson = PublicReturnParametersEnum.buildPublicReturnParametersEnumJsonObject();
        getHaveParamMappingColumn(apiRespParamDTO, DOLLAR, pageResultJson);
        // 在这里得到最后的返回结果，需要遍历一下，如果有重复映射的，不做更改，只有单次映射的，将字段名key去掉
        for (Map.Entry<String, Object> entry : pageResultJson.entrySet()) {
            if (entry.getValue() instanceof String) {
                String value = (String)entry.getValue();
                entry.setValue(value.replace(value.substring(0, value.indexOf(COLON) + 1), ""));
            }
        }
        // 如果三个分页参数都为空的情况，判断是不需要分页参数，直接去掉
        PublicReturnParametersEnum.judgeRemovePageParam(pageResultJson);
        return pageResultJson;
    }

    /**
     * 递归获取存在映射关系的字段（树状从上往下迭代获取存在映射关系的字段）
     *
     * @param apiRespParamDTO 参数树
     * @param pageResultJson 父参数列的表达式（不一定是存在父列，但是先构造好以便子列使用）
     * @param parentExpression 构造pageResultDTO对象结构的JSONObject
     */
    private void getHaveParamMappingColumn(ApiRespParamDTO apiRespParamDTO, String parentExpression, JSONObject pageResultJson){
        if (ObjectUtil.isNotEmpty(apiRespParamDTO)) {
            // 1、构造表达式，如果是根节点BODY需要将字段命大写后拼接上去
            String expression = "";
            if (HttpParamKind.BODY.name().equalsIgnoreCase(apiRespParamDTO.getAssetColumns()) && apiRespParamDTO.getParentId() == 0) {
                expression = parentExpression;
            }else {
                expression = parentExpression  + DOT + apiRespParamDTO.getAssetColumns() ;
            }
            // 2、如果这个参数列中的defaultValue不为空，说明存在映射关系，构造PageResultDTO结构的JsonObject
            if (ObjectUtil.isNotEmpty(apiRespParamDTO.getDefaultValue())) {
                apiRespParamDTO.setExpression(expression);
                Object pageResultJsonValue  = pageResultJson.get(apiRespParamDTO.getDefaultValue().trim());
                buildExpressionJsonObject(apiRespParamDTO, pageResultJson, pageResultJsonValue, PublicReturnParametersEnum.getEnumByAttributeName(apiRespParamDTO.getDefaultValue().trim()));
            }
            if (CollectionUtil.isNotEmpty(apiRespParamDTO.getChildApiRespParams())) {
                for (ApiRespParamDTO apiRespParam : apiRespParamDTO.getChildApiRespParams()) {
                    getHaveParamMappingColumn(apiRespParam, expression, pageResultJson);
                }
            }
        }
    }

    private void buildExpressionJsonObject(ApiRespParamDTO apiRespParamDTO, JSONObject pageResultJson, Object pageResultJsonValue, PublicReturnParametersEnum publicReturnParametersEnum) {
        // 判断是否已经存在字段映射
        if (ObjectUtil.isNotEmpty(pageResultJsonValue)) {
            //判断，有两个以上的字段映射，直接转换成JSONObject
            if (pageResultJsonValue instanceof JSONObject) {
                ((JSONObject) pageResultJsonValue).put(apiRespParamDTO.getAssetColumns(), apiRespParamDTO.getExpression());
            }else {
                // 不是jsonObject的情况，只有映射了一次，格式只有string，以“:”为分隔点，构造一个JSONObject
                JSONObject pageResultJsonObjectValue = new JSONObject();
                String key = ((String) pageResultJsonValue).substring(0, ((String) pageResultJsonValue).indexOf(COLON));
                String value = ((String) pageResultJsonValue).substring(((String) pageResultJsonValue).indexOf(COLON) + 1);
                pageResultJsonObjectValue.put(key, value);
                pageResultJsonObjectValue.put(apiRespParamDTO.getAssetColumns(), apiRespParamDTO.getExpression());
                pageResultJsonValue = pageResultJsonObjectValue;
            }
        }else {
            pageResultJsonValue = apiRespParamDTO.getAssetColumns() + COLON + apiRespParamDTO.getExpression();
        }
        pageResultJson.put(publicReturnParametersEnum.getAttributeName(), pageResultJsonValue);
    }

}
