package com.wakedata.dw.open.function.custom;

/**
 * 自定义函数状态枚举
 * @author luomeng
 * @date 2022/11/1 19:15
 */
public class CustomFunctionStatus {


    /**
     * 函数状态
     */
    public enum StatusEnum{
        /**
         * 1：草稿 2：上线 3：下线
         */
        OUTLINE(1,"草稿"),
        ONLINE(2,"上线"),
        OFFLINE(3,"下线"),
        ;

        /**
         * 代码
         */
        private Integer code;
        /**
         * 描述
         */
        private String desc;

        StatusEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    /**
     * debug状态枚举
     */
    public enum DebugStatusEnum{
        /**
         * 测试状态 1：未测试 2：测试通过 3：测试失败 （只有测试通过的函数才能上线）
         */
        NOT_TEST(1,"未测试"),
        PASS_TEST(2,"测试通过"),
        FAIL_TEST(3,"测试失败"),
        ;

        /**
         * 代码
         */
        private Integer code;
        /**
         * 描述
         */
        private String desc;

        DebugStatusEnum(Integer code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }


}
