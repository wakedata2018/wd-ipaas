package com.wakedata.dw.open.function.custom;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author luomeng
 * @date 2022/11/3 15:08
 */
@Data
@ApiModel(value = "自定义函数支持类型")
public class CustomFunctionSupportVo implements Serializable {

    /**
     * 支持类型
     */
    private List<SupportType> supportTypeList;

    /**
     * 支持语言
     */
    private List<SupportLanguage> supportLanguageList;


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportType{
        /**
         * 类型
         */
        private String type;
        /**
         * 描述
         */
        private String desc;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SupportLanguage{
        /**
         * 语言
         */
        private String language;

        /**
         * 描述
         */
        private String desc;

        /**
         * 示例代码
         */
        private String code;

    }
}
