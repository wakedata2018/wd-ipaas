package com.wakedata.dw.open.constant;

/**
 * 事件算子常量
 *
 * @author wujunqiang
 * @since 2022/11/3 16:10
 */
public class EventConstant {

    /**
     * Kafka安全认证协议
     */
    public static final String KAFKA_SECURITY_PROTOCOL = "SASL_PLAINTEXT";

    /**
     * Kafka SASL机制
     */
    public static final String KAFKA_SASL_MECHANISM = "PLAIN";

    /**
     * Kafka JAAS配置
     */
    public static final String KAFKA_SASL_JAAS_CONFIG = "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"%s\" password=\"%s\";";

}
