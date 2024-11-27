package com.wakedata.dw.open.enums;

import lombok.Getter;
import org.apache.ibatis.type.BaseDbEnum;


/**
 * @author WangChenSheng
 * @descriptor
 * @title XxlJobEnums
 * @date 2022/10/25 14:58
 */
public class XxlJobEnums {

    @Getter
    public enum TaskType implements BaseDbEnum<Integer> {

        /**
         * 任务状态
         */
        TASK_STOP(0),
        TASK_ACTIVE(1);

        TaskType(Integer value) {
            this.value = value;
        }

        private final Integer value;

    }

    @Getter
    public enum TaskExecuteType implements BaseDbEnum<Integer> {

        /**
         * 任务执行状态
         */
        TASK_EXECUTE_PERMANENT(1),
        TASK_EXECUTE_CUSTOM(2);

        TaskExecuteType(Integer value) {
            this.value = value;
        }

        private final Integer value;

    }

}


