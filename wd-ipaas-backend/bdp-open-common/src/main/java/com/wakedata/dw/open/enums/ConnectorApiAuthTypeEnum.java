package com.wakedata.dw.open.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * 连接器API鉴权类型枚举类
 *
 * @author wujunqiang
 * @since 2022/11/18 10:18
 */
@Getter
public enum ConnectorApiAuthTypeEnum {

    /**
     * 不需要鉴权
     */
    NO_AUTHENTICATION(0, "不需要鉴权"),
    /**
     * 企业微信
     */
    ENTERPRISE_WECHAT(1, "企业微信"),
    /**
     * 惟客云
     */
    WAKE_CLOUD(2, "惟客云");

    private final Integer value;

    private final String desc;

    ConnectorApiAuthTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    /**
     * 根据枚举类字符串找到对应枚举类型
     *
     * @param enumName 枚举类名称
     * @return 连接器API鉴权类型枚举类
     */
    public static ConnectorApiAuthTypeEnum of(String enumName) {
        Optional<ConnectorApiAuthTypeEnum> optional = Arrays.stream(ConnectorApiAuthTypeEnum.values()).filter(x -> x.name().equals(enumName)).findFirst();
        return optional.orElseThrow(() -> new IllegalArgumentException("ConnectorApiAuthTypeEnum find error，not found:" + enumName));
    }

}
