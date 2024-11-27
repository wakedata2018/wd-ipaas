package com.wakedata.dw.open.liteflow.component.foreach;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.common.core.constants.CommonConstant;
import com.wakedata.dw.open.condition.ConditionUtils;
import com.wakedata.dw.open.enums.OpenApiMsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.liteflow.NodeRunTimeContext;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import com.wakedata.dw.open.model.api.flow.operator.foreach.BreakCondition;
import com.wakedata.dw.open.parammapping.util.ParamMappingsUtils;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.model.api.flow.operator.foreach.ForOperator;
import com.wakedata.dw.open.parammapping.RequestParamMapping;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.slot.Slot;
import com.yomahub.liteflow.util.LiteFlowProxyUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * for循环node组件
 *
 * @author luomeng
 * @date 2022/12/5 16:27
 */
@Slf4j
public class ForNodeComponent extends AbstractNodeComponent<ForOperator> {

    /**
     * 最大循环次数，防止出现死循环
     */
    private static final int MAX_LOOP_SIZE = 10000;

    /**
     * 循环内部属性,如当前循环次数（从0开始），当前循环的对象值
     */
    public enum LoopFieldEnum {
        /**
         * 属性定义
         */
        COUNTER("counter", "循环遍历时用于统计次数（从0开始）的变量名"),
        CURRENT_ITEM("currentItem", "循环遍历时用于获取当次循环对象的值"),
        LOOP_OBJECT("loopObject", "循环对象值，首次执行时解析到的对象值"),
        LOOP_SIZE("loopSize", "总循环次数，最大不能超过10000");

        /**
         * 属性名称
         */
        private String field;
        /**
         * 描述
         */
        private String desc;

        LoopFieldEnum(String field, String desc) {
            this.field = field;
            this.desc = desc;
        }

        public String getField() {
            return field;
        }

        public String getDesc() {
            return desc;
        }
    }


    /**
     * WHILE循环算子需要强制指定组件类型为WHILE
     * @param type
     */
    @Override
    public void setType(NodeTypeEnum type) {
        super.setType(NodeTypeEnum.WHILE);
    }

    @Override
    public void processCall() throws Exception {
        boolean whileFlag = processWhile();
        Slot slot = this.getSlot();
        Class<?> originalClass = LiteFlowProxyUtil.getUserClass(this.getClass());
        slot.setWhileResult(originalClass.getName(), whileFlag);
    }

    /**
     * 循环条件
     *
     * @return
     * @throws Exception
     */
    public boolean processWhile() throws Exception {
        NodeRunTimeContext nodeRunTimeContext = threadLocal.get();
        ForOperator forOperator = (ForOperator) nodeRunTimeContext.getOperatorContext().getOperator();
        JSONObject results = (JSONObject) ObjectUtil.defaultIfNull(getOperatorResultSet(nodeRunTimeContext, nodeRunTimeContext.getOperator().getName()), new JSONObject());
        JSONObject body = ObjectUtil.defaultIfNull(results.getJSONObject(HttpParamKind.BODY.name()), new JSONObject());
        JSONArray loopObject = (JSONArray) body.get(LoopFieldEnum.LOOP_OBJECT.getField());
        //总循环次数
        Integer loopSize = ObjectUtil.defaultIfNull(body.getInteger(LoopFieldEnum.LOOP_SIZE.getField()), CommonConstant.ZERO);
        //计数器
        Integer counter = ObjectUtil.defaultIfNull(body.getInteger(LoopFieldEnum.COUNTER.getField()), CommonConstant.ZERO);
        //循环项
        Object currentItem = null;
        //首次执行
        if (isFirstExec(loopSize)) {
            counter = CommonConstant.ZERO;
            //按次数
            if (forOperator.getComponent().checkIsCount()) {
                loopObject = null;
                loopSize = ObjectUtil.defaultIfNull(forOperator.getComponent().getLoopCount(),CommonConstant.ZERO);
            } else {
                //解析参数
                loopObject = (JSONArray) parseExpression(nodeRunTimeContext, forOperator);
                loopSize = loopObject.size();
                if(loopSize > CommonConstant.ZERO){
                    currentItem = loopObject.get(counter);
                }
            }
        } else {
            if (!isContinue((counter + 1), loopSize)) {
                addLog("已全部执行，循环结束：" + JSONUtil.toJsonStr(results));
                return false;
            }
            currentItem = ObjectUtil.isNull(loopObject) ? null : loopObject.get(counter);

        }
        counter += CommonConstant.ONE;
        //设置循环数据
        setForResults(nodeRunTimeContext, results, body, loopObject, loopSize, counter, currentItem);
        //是否继续
        return !isBreakLoop(forOperator, nodeRunTimeContext.getApiFlowSlot().getFullOperatorResults(), counter) && isContinue(counter, loopSize);
    }

    /**
     * 是否退出循环
     * @param forOperator
     * @param results
     * @param counter
     * @return
     */
    private boolean isBreakLoop(ForOperator forOperator, JSONObject results, Integer counter) {
        BreakCondition breakCondition = forOperator.getComponent().getBreakCondition();
        boolean breakResult = false;
        if(ObjectUtil.isNotNull(breakCondition) && breakCondition.checkBreak()){
            if(breakCondition.checkIsCount()){
                breakResult = counter >= breakCondition.getCount();
            }else{
                breakResult = ConditionUtils.exec(breakCondition.getCondition(), results);
            }
            addLog("执行第" + counter + "次退出循环条件校验，校验结果：" + breakResult);
        }
        return breakResult;
    }

    /**
     * 解析表达式
     * @param nodeRunTimeContext
     * @param forOperator
     * @return
     */
    private Object parseExpression(NodeRunTimeContext nodeRunTimeContext, ForOperator forOperator) {
        RequestParamMapping requestParamMappings = forOperator.getComponent().getLoopCondition();
        requestParamMappings.setField(LoopFieldEnum.LOOP_OBJECT.getField());
        ParamMappingsUtils.MappingFieldResults mappingFieldResults = ParamMappingsUtils.alignFieldValues(nodeRunTimeContext.getApiFlowSlot().getFullOperatorResults(), Collections.singletonList(requestParamMappings));
        Object res = mappingFieldResults.getFieldValues().get(LoopFieldEnum.LOOP_OBJECT.getField());
        //只支持解析数组
        if (ObjectUtil.isNull(res) || res instanceof JSONObject) {
            addLog("循环条件数据解析为空，condition：" + JSONUtil.toJsonStr(requestParamMappings) + ",result：" + res);
            throw new OpenException(OpenApiMsgCodeEnum.w_api_graph_execute_for_operator_error);
        }
        if(res instanceof List){
            return JSONArray.parseArray(JSON.toJSONString(res));
        }
        return res;
    }

    /**
     * 是否是第一次执行
     * @param loopSize
     * @return
     */
    private boolean isFirstExec(Integer loopSize) {
        return loopSize < CommonConstant.ONE;
    }

    /**
     * 是否继续
     *
     * @param counter
     * @param loopSize
     * @return
     */
    private boolean isContinue(Integer counter, Integer loopSize) {
        return counter <= loopSize && counter < MAX_LOOP_SIZE;
    }

    /**
     * 设置for循环数据
     *
     * @param nodeRunTimeContext
     * @param results
     * @param body
     * @param loopObject
     * @param loopSize
     * @param counter
     * @param currentItem
     */
    private void setForResults(NodeRunTimeContext nodeRunTimeContext, JSONObject results, JSONObject body, JSONArray loopObject, Integer loopSize, Integer counter, Object currentItem) {
        body.put(LoopFieldEnum.COUNTER.getField(), counter);
        body.put(LoopFieldEnum.CURRENT_ITEM.getField(), currentItem);
        body.put(LoopFieldEnum.LOOP_OBJECT.getField(), loopObject);
        body.put(LoopFieldEnum.LOOP_SIZE.getField(), loopSize);
        results.put(HttpParamKind.BODY.name(), body);
        storeOperatorResultSet(nodeRunTimeContext, results);
        addLog("执行第" + counter + "次循环，循环数据：" + JSONUtil.toJsonStr(results));
    }

    @Override
    protected JSON simpleProcessInternal(NodeRunTimeContext nodeRunTimeContext, JSONObject currentRequestParams) throws Exception {
        return null;
    }

    @Override
    protected OpenApiMsgCodeEnum getOpenApiMsgEnum() {
        return OpenApiMsgCodeEnum.w_api_graph_execute_for_operator_error;
    }
}
