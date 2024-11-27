package com.wakedata.dw.open.model.event;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Set;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2021/10/29 11:40
 */
@Getter
@Setter
@ToString
@Slf4j
public class SubscribeRecordDTO implements Serializable {
    private static final long serialVersionUID = 3140666317849261382L;

    @ApiModelProperty("关联事件ID")
    @NotBlank(message = "订阅事件ID不能为空")
    private String eventId;

    @ApiModelProperty("订阅地址id")
    @NotEmpty(message = "订阅地址id不能为空")
    private Set<String> subscribeAddressIds;
}
