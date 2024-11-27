package com.wakedata.dw.open.model.event;

import com.wakedata.dw.open.model.BasePo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;

/**
 * @author wanghu@wakedata.com
 * @title DO基类
 * @date 2021/7/18
 * @since v1.0.0
 */
@Getter
@Setter
@ToString
public class EventBasePo extends BasePo {

    /**
     * 逻辑有效位 1-有效, 0-无效
     */
////    @Column(name = "DELETED_FLAG")
//    private Integer deletedFlag;

    @Column(name = "CREATE_USER")
    private String createUser;
    @Column(name = "UPDATE_USER")
    private String updateUser;

    public void clearBaseDO() {
        setCreateTime(null);
        setUpdateTime(null);
//        setDeletedFlag(null);
    }

}
