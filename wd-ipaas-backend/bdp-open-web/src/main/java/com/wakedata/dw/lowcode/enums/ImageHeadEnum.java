package com.wakedata.dw.lowcode.enums;

import com.wakedata.common.core.base.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author wanghu@wakedata.com
 * @title 图片文件头部枚举
 * @date 2021/11/30
 * @since v1.0.0
 */
@Getter
@AllArgsConstructor
public enum ImageHeadEnum implements BaseEnum {

    /**
     * JPG
     */
    JPG("jpg", "FFD8FFE0"),
    JPEG("jpeg", "FFD8FFE0"),
    PNG("png", "89504E47"),
    GIF("gif", "47494638"),
    BMP("bmp", "424D4E9B");

    /**
     * 文件类型
     */
    private final String value;
    /**
     * 文件头部16进制
     */
    private final String desc;

    public static boolean existFileHead(String hex) {
        for (ImageHeadEnum headEnum : ImageHeadEnum.values()) {
            if (headEnum.getDesc().equals(hex)) {
                return true;
            }
        }
        return false;
    }

}
