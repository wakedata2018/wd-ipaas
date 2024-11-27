package com.wakedata.dw.open.model.api.flow.operator.judge;

import com.wakedata.dw.open.condition.Condition;
import com.wakedata.dw.open.model.api.rule.ApiRuleAttr;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判断算子内部对象
 *
 * @author wujunqiang
 * @since 2022/9/19 17:11
 */
@Data
public class JudgeParam {


    /**
     * 存放判断算子的出边线条连接信息
     */
    private List<ApiRuleAttr> apiAttrs;

    /**
     * 比较值内容
     */
    private Condition comparisonValue;

    public JudgeParam() {
        apiAttrs = new ArrayList<>();
        comparisonValue = new Condition();
    }

    /**
     * 根据判断算子结果返回出边算子Id
     */
    public String getJudgeOutputOperatorId(Boolean result, String judgeOperatorId) {
        //过滤出当前判断算子的出边，只有两条（是true/false）
        List<ApiRuleAttr> apiRuleAttrs = this.apiAttrs.stream().filter(a -> a.getFromOperatorId().equals(judgeOperatorId)).collect(Collectors.toList());
        //根据校验后的结果，获取出边算子Id
        for (ApiRuleAttr apiRuleAttr : apiRuleAttrs) {
            if (apiRuleAttr.getRuleName().equalsIgnoreCase(result.toString())) {
                return apiRuleAttr.getToOperatorId();
            }
        }
        return null;
    }
}
