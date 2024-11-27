package com.wakedata.dw.open.function.custom;

/**
 * 自定义函数模板
 *
 * @author luomeng
 * @date 2022/10/31 16:50
 */
public enum CustomFunctionTemplateEnum {
    /**
     * 脚本
     */
    GROOVY_SCRIPT("groovy","groovy脚本", "package com.wakedata.dw.open.function.custom\n" +
            "\n" +
            "import com.alibaba.fastjson.JSONObject\n" +
            "import java.util.*\n" +
            "/**\n" +
            " * Groovy Script自定义函数实现\n" +
            " */\n" +
            "class CustomFunction  {\n" +
            "\n" +
            "   /**\n" +
            "  \t* 函数实现\n" +
            "    */\n" +
            "    //xxx\n" +
            "    \n" +
            "}"),
//    PYTHON_SCRIPT("","");
    ;

    /**
     * 语言
     */
    private String language;
    /**
     * 描述
     */
    private String desc;
    /**
     * 模板代码
     */
    private String code;


    CustomFunctionTemplateEnum(String language,String desc, String code) {
        this.language = language;
        this.desc = desc;
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public String getDesc() {
        return desc;
    }

    public String getCode() {
        return code;
    }
}
