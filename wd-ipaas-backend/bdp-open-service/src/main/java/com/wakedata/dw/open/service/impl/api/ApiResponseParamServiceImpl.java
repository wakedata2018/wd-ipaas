package com.wakedata.dw.open.service.impl.api;

import com.google.common.collect.Lists;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.IFunc;
import com.wakedata.dw.open.mapper.api.ApiResponseParamMapper;
import com.wakedata.dw.open.mapper.connector.ConnectorApiResponseParamMapper;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.ApiResponseParamPo;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.connector.ConnectorApiResponseParamPo;
import com.wakedata.dw.open.parammapping.ParamMappingTypeEnum;
import com.wakedata.dw.open.parammapping.util.JsonPathHelper;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.ApiResponseParamService;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.api.dto.BuildExpressionDTO;
import com.wakedata.dw.open.service.api.dto.LiteFlowResultTemplateDTO;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.impl.api.helper.ApiResponseHelper;
import com.wakedata.dw.open.swagger.SwaggerConstants;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author wanghu@wakedata.com
 * @title API响应参数(DwOpenApiResponseParam)表服务接口实现类
 * @date 2021/12/10
 * @since v1.0.0
 */
@Slf4j
@Service
public class ApiResponseParamServiceImpl
    extends BaseServiceImpl<ApiResponseParamPo, ApiResponseParamMapper> implements ApiResponseParamService {

    @Autowired
    private ConnectorApiResponseParamMapper connectorApiResponseParamMapper;

    private static final String COMMA = ".";
    /**
     * 根节点的引用值表达式
     */
    private static final String ROOT_EXPRESSION = "$.start.BODY";
    @Autowired
    @Override
    protected void init(CurdService<ApiResponseParamPo> curdService, ApiResponseParamMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public List<ApiRespParamDTO> listByApiId(Integer apiId) {
        return getApiRespParamDTOS(apiId,DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT);
    }

    @Override
    public List<ApiRespParamDTO> getApiRespParamByConnectorId(Long connectorApiId) {
        Example example = new Example(ConnectorApiResponseParamPo.class);
        example.createCriteria().andEqualTo("connectorApiId", connectorApiId);
        List<ConnectorApiResponseParamPo> responseParamPos = connectorApiResponseParamMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(responseParamPos)) {
            return Collections.emptyList();
        }

        List<ApiRespParamDTO> paramDtoList = new ArrayList<>(responseParamPos.size());
        for (ConnectorApiResponseParamPo responseParamPo : responseParamPos) {
            ApiRespParamDTO apiRespParamDTO = new ApiRespParamDTO();
            apiRespParamDTO.setId(Math.toIntExact(responseParamPo.getId()));
            apiRespParamDTO.setAssetColumns(responseParamPo.getAssetColumns());
            apiRespParamDTO.setAssetDataType(responseParamPo.getAssetDataType());
            apiRespParamDTO.setDescription(responseParamPo.getDescription());
            apiRespParamDTO.setType(responseParamPo.getHttpParamKind());
            apiRespParamDTO.setBusinessType(DataAssetEnums.ApiResponseBusinessType.HTTP_RESULT);
            apiRespParamDTO.setParentId(DwOpenConstant.RESPONSE_PARENT_ID);
            apiRespParamDTO.setResponsePostData(responseParamPo.getResponsePostData());
            paramDtoList.add(apiRespParamDTO);
        }
        return paramDtoList;
    }

    private List<ApiRespParamDTO> getApiRespParamDTOS(Integer apiId,DataAssetEnums.ApiResponseBusinessType apiResponseBusinessType) {
        ApiResponseParamPo query = new ApiResponseParamPo();
        query.setDataAssetId(apiId);
        query.setBusinessType(apiResponseBusinessType);
        List<ApiResponseParamPo> responseParamPos = getMapper().select(query);

        if (CollectionUtils.isEmpty(responseParamPos)) {
            return Collections.emptyList();
        }
        // 父级id->响应参数列表
        Map<Integer, List<ApiResponseParamPo>> parentIdToApiResp = responseParamPos.stream()
            .collect(Collectors.groupingBy(ApiResponseParamPo::getParentId));
        return ApiResponseHelper.buildApiRespParamTree(parentIdToApiResp.get(0), parentIdToApiResp);
    }

    @Override
    public List<ApiRespParamDTO> findLiteflowResult(Integer apiId) {
        return getApiRespParamDTOS(apiId,DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
    }

    @Override
    public void saveLiteFlowResult(LiteFlowResultTemplateDTO liteFlowResultTemplateDTO, IpaasUserInfo ipaasUserInfo) {
        Integer dataAssetApiId = liteFlowResultTemplateDTO.getDataAssetApiId();
        if (null == dataAssetApiId){
            return;
        }
        List<ApiRespParamDTO> apiRespParamDTOS = liteFlowResultTemplateDTO.getApiRespParamDTOS();
        if (apiRespParamDTOS.isEmpty()){
            return;
        }
        apiRespParamDTOS.forEach(ApiRespParamDTO->{
            this.saveResponseNode(ApiRespParamDTO,0, dataAssetApiId, ipaasUserInfo);
        });
    }

    private void saveResponseNode(ApiRespParamDTO node,Integer parentId,Integer dataAssetApiId,IpaasUserInfo ipaasUserInfo) {
        if (Objects.isNull(node)) {
            return;
        }
        ApiResponseParamPo apiResponseParamPo = com.wakedata.common.core.util.BeanUtil.copy(node, ApiResponseParamPo.class);
        apiResponseParamPo.setDataAssetId(dataAssetApiId);
        apiResponseParamPo.setParentId(parentId);
        apiResponseParamPo.setLesseeId(ipaasUserInfo.getLesseeId());
        apiResponseParamPo.setCreateBy(ipaasUserInfo.getName());
        apiResponseParamPo.setUpdateBy(ipaasUserInfo.getName());
        apiResponseParamPo.setCreateTime(new Date());
        apiResponseParamPo.setUpdateTime(new Date());
        apiResponseParamPo.setResponded(0);
        apiResponseParamPo.setBusinessType(DataAssetEnums.ApiResponseBusinessType.LITEFLOW_RESULT);
        if (StringUtils.isBlank(apiResponseParamPo.getParamValueType())){
            apiResponseParamPo.setParamValueType(ParamMappingTypeEnum.REFERENCE_TYPE.getType());
        }
        this.add(apiResponseParamPo);

        // 现响应参数数据存储不以树状结构存储,因此不走以下递归代码
        List<ApiRespParamDTO> childApiRespParams = node.getChildApiRespParams();
        if (CollectionUtils.isEmpty(childApiRespParams)) {
            return;
        }
        for (ApiRespParamDTO child : childApiRespParams) {
            saveResponseNode(child,apiResponseParamPo.getId(),dataAssetApiId,ipaasUserInfo);
        }
    }

    @Override
    public String convertColumnToExpression(BuildExpressionDTO dto, String expressionType) {
        return convertResultColumn(dto,expressionType);
    }

    public String convertResultColumn(BuildExpressionDTO dto, String expressionType) {
        // start节点，不会存在多级的情况
        if (null!=dto.getNodeName() && dto.getNodeName().equals(JsonPathHelper.START_NODE_NAME)){
            String startExpression = buildExpressionIfStartNode(dto,DataAssetEnums.ExpressionType.LITEFLOW_RESULT);
            if (Objects.nonNull(startExpression)) {
                return startExpression;
            }
        }
        return buildExpression(dto,expressionType);
    }

    private String buildExpression(BuildExpressionDTO dto , String expressionType) {
        Map<Integer, ApiRespParamDTO> apiResponseMap = new HashMap<>();

        // 校验选中的参数节点所在的树
        List<ApiRespParamDTO> apiRespParamDTOS = dto.getApiRespParamDTOS();
        checkNotNull(apiRespParamDTOS);

        // 校验选中的参数节点
        ApiRespParamDTO apiRespParam = dto.getApiRespParam();
        checkNotNull(apiRespParam);
        // 判断是选择根节点整个对象的引用值，直接返回
        if (HttpParamKind.BODY.name().equalsIgnoreCase(apiRespParam.getAssetColumns()) && apiRespParam.getParentId().equals(DwOpenConstant.RESPONSE_PARENT_ID)) {
            return ROOT_EXPRESSION;
        }
        if (Objects.isNull(apiRespParam.getId())) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        //解析树状结构的List<ApiRespParamDTO>,并转换成Map<id,ApiRespParamDTO>
        ApiResponseHelper.buildApiResponseMap(apiRespParamDTOS, apiResponseMap);

        //选中的节点
//        ApiResponseParamPo childRespParam = getMapper().selectByPrimaryKey(apiRespParam.getId());
        ApiRespParamDTO childRespParam = apiRespParam;
        checkNotNull(childRespParam);

        String column = childRespParam.getAssetColumns();
        List<String> allColumn = Lists.newArrayList(column);
        //给字段类型是array系列的字段名加上[0]，和将父字段名称加入到allcolumn字符数组中，从叶子到根处理字段
        while (childRespParam.getParentId() != 0) {
//            childRespParam = getMapper().selectByPrimaryKey(childRespParam.getParentId());
            //获取当前字段的父级字段对象（包括底下的子字段对象）
            childRespParam = apiResponseMap.get(childRespParam.getParentId());
            // 判断是根节点,将字段名改成大写，不改大写引用值获取不成功
            if (HttpParamKind.BODY.name().equalsIgnoreCase(childRespParam.getAssetColumns()) && childRespParam.getParentId().equals(DwOpenConstant.RESPONSE_PARENT_ID)) {
                childRespParam.setAssetColumns(HttpParamKind.BODY.name());
            }
            checkNotNull(childRespParam);
            //判断字段类型是不是array，是的话字段名更新为column+“[0]”
            column = wrapColumnIfArray(childRespParam.getAssetDataType(), childRespParam.getAssetColumns());
            allColumn.add(0, column);
        }
        String nodeName = dto.getNodeName();
        // 如果节点名称为空不添加节点
        if (StringUtils.isBlank(nodeName)){
            return buildExpressionIfNonStartNode(allColumn, dto.getApiRespParam());
        }
//        // 如果是算子之间构造表达式，无需添加节点信息
//        if (DataAssetEnums.ExpressionType.BETWEEN_OPERATOR.getValue().equals(expressionType)){
//            return buildExpressionIfNonStartNode(allColumn, dto.getApiRespParam());
//        }
        // 如果是结束模板构造表达式，需要添加对应的节点信息
        String resultExpression = buildExpressionIfNonStartNode(allColumn, dto.getApiRespParam())
                .replace(JsonPathHelper.PRE_SPLIT, JsonPathHelper.PRE_SPLIT + nodeName + COMMA );

        // 如果是更新变量算子的变量构建，需要去remove部分字段($. BODY [0])
        if (Objects.equals(expressionType, DataAssetEnums.ExpressionType.UPDATE_VARIABLE_OPERATOR.getValue())){
            resultExpression = buildUpdateVariable(resultExpression);
        }
        return resultExpression;
    }


    /**
     * 处理更新变量算子变量算子名称引用
     * $.创建变量算子名称.BODY.参数位置[] => 创建变量算子名称.参数位置
     *
     * @param jsonPath jsonPath
     * @return 更新变量算子变量名称
     */
    public static String buildUpdateVariable(String jsonPath){
        // 依次移除jsonPath中的$.  BODY  []
        String variableKey = StringUtils.removeStart(jsonPath, IFunc.PRE_SPLIT);
        variableKey = StringUtils.remove(variableKey,DwOpenConstant.JOIN_POINT.concat(HttpParamKind.BODY.name()));

        if (StringUtils.endsWith(jsonPath,DwOpenConstant.MIDDLE_BRACKET_RIGHT)){
            variableKey = StringUtils.substringBeforeLast(variableKey,DwOpenConstant.MIDDLE_BRACKET_LEFT);
        }
        return variableKey;
    }

    public String wrapColumnIfArray(String columnType, String column) {
        return StringUtils.containsIgnoreCase(columnType,SwaggerConstants.ARRAY) ? column + "[0]" : column;
    }

    @Override
    public Integer clearByDataAssertId(Integer dataAssetApiId) {
        if (dataAssetApiId == null) {
            throw new OpenException(MsgCodeEnum.w_api_id_is_not_empty);
        }
        ApiResponseParamPo apiResponseParamPo = new ApiResponseParamPo();
        apiResponseParamPo.setDataAssetId(dataAssetApiId);
        return getMapper().delete(apiResponseParamPo);
    }

    public String buildExpressionIfNonStartNode(List<String> allRespParams, ApiRespParamDTO endApiRespParam) {
        checkNotNull(endApiRespParam);
        checkNotNull(endApiRespParam.getType());

        //body类型
        if (endApiRespParam.getType() == HttpParamKind.BODY) {

            //数据表类型添加data[0]
            if (Boolean.TRUE.equals(endApiRespParam.getNormalTable())) {
                allRespParams.add(0, "data[0]");
            }
            allRespParams.add(0, JsonPathHelper.PRE_SPLIT_NO_COMMA);
            return StringUtils.join(allRespParams, COMMA);
        } else {
            // HEAD QUERY(开始算子独有)类型
            return JsonPathHelper.PRE_SPLIT
                + endApiRespParam.getType() + COMMA + escapeStr(StringUtils.join(allRespParams, COMMA));

        }

    }

    // 第二个方法入参可以移除
    private String buildExpressionIfStartNode(BuildExpressionDTO dto, DataAssetEnums.ExpressionType expressionType) {
        if (Objects.isNull(dto.getBindApiCondition())) {
            return null;
        }

        ApiConditionPo reqApiConditionPo = dto.getReqApiCondition();
        checkNotNull(reqApiConditionPo);
        // 根据不同类型构建不同的表达式前缀
        String prefixSplit = JsonPathHelper.START_SPLIT;
        //等于body,直接拼接$.BODY
        if (HttpParamKind.BODY == reqApiConditionPo.getHttpParamKind()) {
            return prefixSplit + HttpParamKind.BODY.toString();
            //等于query,直接拼接$.name
        } else if (HttpParamKind.QUERY == reqApiConditionPo.getHttpParamKind()) {
            return prefixSplit + dto.getBindApiCondition().getAssetColumns();
        } else if (HttpParamKind.HEAD == reqApiConditionPo.getHttpParamKind()) {
            return prefixSplit + escapeStr(dto.getBindApiCondition().getAssetColumns());
        } else {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
    }

    /**
     * 转义字符串"-"
     *
     * @param head 请求参数key
     * @return 转义后字符串
     */
    private String escapeStr(String head) {
        //head参数不能为空
        if (StringUtils.isBlank(head)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
        //转义字符串
        int index = head.indexOf("-");
        if (index != -1) {
            StringBuilder stringBuilder = new StringBuilder(head);
            stringBuilder.insert(index, "\\");
            return stringBuilder.toString();
        }

        return head;
    }

    public void checkNotNull(Object param) {
        if (Objects.isNull(param)) {
            throw new OpenException(MsgCodeEnum.w_wrong_argument);
        }
    }

    @Override
    public List<ApiResponseParamPo> getApiResponseData(Long lesseeId, Integer dataAssetApiId) {
        ApiResponseParamPo apiResponseParamPo = new ApiResponseParamPo();
        apiResponseParamPo.setLesseeId(lesseeId);
        apiResponseParamPo.setDataAssetId(dataAssetApiId);
        return getMapper().select(apiResponseParamPo);
    }
}
