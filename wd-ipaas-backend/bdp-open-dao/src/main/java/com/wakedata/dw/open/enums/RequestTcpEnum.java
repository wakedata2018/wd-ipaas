package com.wakedata.dw.open.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 协议枚举
 *
 * @author caoshuang
 * @date 2021/10/20
 */
@Deprecated
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RequestTcpEnum {

    HTTP(1)
    ,KAFKA(2)
    ,ROCKET_MQ(3);

    private int val;

    public static RequestTcpEnum covert(int val){
        for(RequestTcpEnum e : RequestTcpEnum.values()){
            if(e.getVal() == val){
                return e;
            }
        }
        return null;
    }
}
