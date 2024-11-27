package com.wakedata.dw.open.model.api.flow.operator.crontab;//package com.wakedata.dw.open.model.api.flow.operator.crontab;
//
//import com.wakedata.dw.open.enums.CronCycleUnitEnum;
//import com.wakedata.dw.open.model.api.flow.operator.TransformOperator;
//import lombok.Data;
//
///**
// * CrontabOperator 定时任务类型算子
// *
// * @author 佟蕊 (待删除)
// */
//@Data
//public class CrontabOperator
//        extends TransformOperator.MultiInputOutputEdge{
//
//    private String taskName;
//    private String taskDescription;
//    private String cronExpression;
//
//    /**
//     * 执行周期值
//     */
//    private Integer cycleValue;
//
//    /**
//     * 执行周期单位
//     *
//     * @see CronCycleUnitEnum
//     */
//    private Integer cycleUnit;
//
//    @Override
//    public void accept(Visitor visitor) throws Exception {
//        visitor.visit(this);
//    }
//}
