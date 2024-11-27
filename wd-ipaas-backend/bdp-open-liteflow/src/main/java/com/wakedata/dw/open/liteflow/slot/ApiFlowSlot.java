package com.wakedata.dw.open.liteflow.slot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wakedata.dw.open.liteflow.component.AbstractNodeComponent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ZhangXueJun
 * @title ApiSlot 数据上下文
 * @date 2021/5/7 14:13
 * @projectName dw-open
 * @description
 */
@Slf4j
public class ApiFlowSlot{

    /**
     * 所有node component 原始返回
     */
    private final JSONObject fullOperatorResults;
    private final JSONObject sinkOperatorResults;
    @Getter
    @Setter
    private Exception exception;

    /**
     * 判断流程是否已经经过判断算子或者分支算子（true->已经过，false->没有经过）
     */
    @Getter
    @Setter
    private Boolean isHaveJudgeOperatorBefore;

    /**
     * 存放流程经过判断算子后，下一步需要执行的算子Id集合
     */
    @Getter
    @Setter
    private Set<String> afterJudgeOperatorIds;

    /**
     * 调试信息
     */
    private StringBuffer notepad = new StringBuffer();

    public ApiFlowSlot() {
        this.fullOperatorResults = new JSONObject();
        this.sinkOperatorResults = new JSONObject();
        this.afterJudgeOperatorIds = new HashSet<>();
        this.isHaveJudgeOperatorBefore = Boolean.FALSE;
    }

    /**
     * 存放结果
     *
     * @param operatorName 节点名
     * @param sink         暂时不知道是什么，待补充 TODO
     * @param results      需要存储的数据对象
     */
    public void storeOperatorResultSet(String operatorName, boolean sink, Object results) {
        JSON jsonResults = AbstractNodeComponent.jsonParse(results);
        fullOperatorResults.put(operatorName, jsonResults);
        if (sink) {
            sinkOperatorResults.put(operatorName, jsonResults);
        }
    }

    /**
     * 存放开始节点结果
     * @param operatorName 节点名
     * @param results 需要存储的数据对象
     */
    public void storeStartResult(String operatorName,JSONObject results) {
        fullOperatorResults.put(operatorName, results);
    }

    public JSON getOperatorResultSet(String operatorName) {
        return (JSON) fullOperatorResults.get(operatorName);
    }

    /**
     * 获取最终结果
     *
     * @return
     * @param accessFields
     */
    public JSON getFinalOutputs(Set<String> accessFields) {
        if (sinkOperatorResults.keySet().containsAll(accessFields) && sinkOperatorResults.size() == accessFields.size()) {
            return sinkOperatorResults;
        }

        JSONObject finalOutputs = new JSONObject();
        for (String accessField : accessFields) {
            finalOutputs.put(accessField, sinkOperatorResults.get(accessField));
        }
        return finalOutputs;
    }

    public JSONObject getFullOperatorResults() {
        return fullOperatorResults;
    }

    public StringBuffer getNotepad() {
        return notepad;
    }

    public void addLog(String log) {
        notepad.append(log);
        notepad.append("\n");
    }

    public boolean isHappenError() {
        return exception != null;
    }

}
