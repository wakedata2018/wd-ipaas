package com.wakedata.dw.open.markdown.constant;

import lombok.Getter;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 16:57:56
 */
@Getter
public enum TitleLevelEnum {
    /**/
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4),
    FIFTH(5)
    ;

    TitleLevelEnum(Integer level) {
        this.value = level;
    }

    private final Integer value;

    public Integer getLevel(){
        return value;
    }

}
