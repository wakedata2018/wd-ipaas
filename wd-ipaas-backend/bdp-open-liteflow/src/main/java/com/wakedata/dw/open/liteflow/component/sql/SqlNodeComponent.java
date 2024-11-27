package com.wakedata.dw.open.liteflow.component.sql;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.common.redis.lock.module.LockInfo;
import com.wakedata.dw.helper.RedisLockHelper;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.liteflow.slot.ApiFlowSlot;
import com.wakedata.dw.open.model.api.ApiConditionPo;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlComponent;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlOperator;
import com.wakedata.dw.open.model.api.flow.operator.sql.SqlOperatorParam;
import com.wakedata.dw.open.model.datasource.DataSourcePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.openapi.OpenApiDataSourceHelper;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.service.api.DataAssetService;
import com.wakedata.dw.open.service.utils.RequestParamUtils;
import com.wakedata.dw.open.utils.JsonUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * sql算子LiteFlow组件
 *
 * @author zhengqinghui@wakedata.com
 * @date 2023/2/14 19:05
 */
public class SqlNodeComponent extends AbstractNodeComponent<SqlOperator> {

    @Resource
    private DataAssetService dataAssetService;

    @Resource
    private OpenApiDataSourceHelper dataSourceHelper;


    /**
     * 获取请求参数集合
     *
     * @return 请求参数集合
     */
    @Override
    protected List<RequestParamMapping> getRequestParamMappings() {
        SqlOperator operator = (SqlOperator) threadLocal.get().getOperator();
        return operator.getRequestParamMappings();
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_for_sql_operator_error;
    }

    @Override
    public void processInternal() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ApiFlowSlot apiFlowSlot = nodeRunTimeContext.getApiFlowSlot();
        // 1、拿到sql算子对应的Operator、以及SqlComponent组件
        SqlOperator sqlOperator = (SqlOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        SqlComponent sqlComponent = ObjectUtil.isNotEmpty(sqlOperator.getComponent()) ? sqlOperator.getComponent() : new SqlComponent();
        SqlOperatorParam sqlOperatorParam = sqlOperator.getComponent().getSqlOperatorParam();
        // 获取请求参数
        JSONObject currentRequestParams = (JSONObject) nodeRunTimeContext.getCurrentRequestParams();
        // 2、打印sql算子获取参数运行日志(在beforeProcessInternal()这个方法处理参数映射的时候已经打印了请求参数的日志)
        // 生成分页参数
        Integer pageNo = JsonUtil.getPageParam(currentRequestParams, RequestParamUtils.PAGE_NO, PageQuery.DEFAULT_PAGE_NO);
        Integer pageSize = JsonUtil.getPageParam(currentRequestParams, RequestParamUtils.PAGE_SIZE, PageQuery.DEFAULT_PAGE_SIZE);
        String orderBy = JsonUtil.getParam(currentRequestParams, RequestParamUtils.ORDER_BY, StringUtils.EMPTY);
        // 3、调用解析sql语句的逻辑，解析得到sql类型api运行所需要的参数（好像不需要调用解析语句的逻辑，前端调用就可以了）
        DataSourcePo dataSourcePo = dataSourceHelper.getDataSourcePo(sqlOperatorParam.getDataSourceId());
        // 返回有权限的列(未做列授权，默认返回sql语句解析出来的所有列)
        HashSet<String> accessRuleFields = (HashSet<String>) (CollectionUtils.isNotEmpty(sqlComponent.getResults())
                ? sqlComponent.getResults().stream().map(ApiConditionPo::getAssetColumns).collect(Collectors.toSet()) : null);
        // 5、封装参数、调用sql类型api运行逻辑
        LockInfo lockInfo = RedisLockHelper.buildLockInfoFromLiteFlow(currentRequestParams, sqlComponent.getApiAttr(), sqlOperator.getName());
        Object data = dataAssetService.readDataAssetDataBySql(dataSourcePo, sqlOperatorParam.getSql(), accessRuleFields, sqlComponent.getParameters(), currentRequestParams, pageNo, pageSize, orderBy, lockInfo);
        // 6、按照sql能力分类，决定使用PageResultDTO或者ResultDTO对返回结果进行封装
        JSONObject result;
        if (DataAssetEnums.DataApiOperationType.QUERY.equals(sqlComponent.getSqlOperationType())) {
            long count = dataAssetService.getDataCount(dataSourcePo, sqlOperatorParam.getSql(), null, DataAssetEnums.DataApiType.CUSTOM_SQL, sqlComponent.getParameters(), currentRequestParams);
            PageResultDTO<Object> pageResultDTO = PageResultDTO.success(data);
            pageResultDTO.setPageSize(pageSize);
            pageResultDTO.setPageNo(pageNo);
            pageResultDTO.setTotalCount(count);
            result = JSON.parseObject(JSON.toJSONString(pageResultDTO));
        } else {
            ResultDTO<Object> resultDTO = ResultDTO.success(data);
            result = JSON.parseObject(JSON.toJSONString(resultDTO));
        }
        // 7、将sql算子运行结果存放到全局参数池中
        storeOperatorResultSet(nodeRunTimeContext, result);
        // 8、打印sql算子运行结果
        apiFlowSlot.addLog(DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + String.format(" SQL算子[%s]: 执行结果....！\n", sqlOperator.getName()) + ParamMappingsUtils.prettyFormatJson(result));
    }

    @Override
    public void processCall() throws Exception {
        // 1、处理请求参数映射
        beforeProcessInternal();

        // 2、内部处理算子
        processInternal();

        // 3、处理响应参数映射
        afterProcessInternal();
    }

}
