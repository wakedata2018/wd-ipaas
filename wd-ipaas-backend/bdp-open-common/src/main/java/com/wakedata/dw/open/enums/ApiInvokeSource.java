package com.wakedata.dw.open.enums;

/**
 * @author WangChenSheng
 * @descriptor API的调用来源
 * @title ApiInvokeSource
 * @date 2022/11/1 19:05
 */
public enum ApiInvokeSource {

    API_EXTERNAL(1,"API外部调用"),

    API_TEST(2,"平台内部测试"),

    API_XXL_JOB(3,"定时任务"),

    API_EVENT_RECEIVE(4,"事件接收");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 类型
     */
    private final String value;

    ApiInvokeSource(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getValue() {
        return this.value;
    }

    public static ApiInvokeSource getEnumByValue(String value) {
        for (ApiInvokeSource sourceEnum : ApiInvokeSource.values()) {
            if (sourceEnum.getValue().equals(value)) {
                return sourceEnum;
            }
        }
        return null;
    }

}
