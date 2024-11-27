package com.wakedata.dw.open.markdown.constant;

/**
 * 代码块语言类型
 *
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 15:23:55
 */
public enum CodeLanguageEnum {

    /**/
    JAVA("JAVA"),
    JSON("JSON"),
    XML("XML"),
    C("C"),
    CPLUSPLUS("C++"),
    JAVASCRIPT("JAVASCRIPT");

    private final String desc;

    CodeLanguageEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
