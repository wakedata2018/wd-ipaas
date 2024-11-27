package com.wakedata.dw.open.function;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * 函数枚举集合
 * @author luomeng
 * @date 2022/8/18 11:14
 */
public interface FunctionEnumSet {

    /**
     * 函数名称
     *
     * @return
     */
    String method();

    /**
     * 返回类型
     *
     * @return
     */
    String returnType();

    /**
     * 函数参数
     *
     * @return
     */
    String param();

    /**
     * 参数说明
     *
     * @return
     */
    String paramDesc();

    /**
     * 示例
     *
     * @return
     */
    String example();

    /**
     * 函数说明
     *
     * @return
     */
    String description();

    /**
     * 枚举对象
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ApiModel(value = "函数内容")
    class FunctionEnumVo {
        @ApiModelProperty("函数名")
        private String method;
        @ApiModelProperty("返回类型")
        private String returnType;
        @ApiModelProperty("函数参数")
        private String param;
        @ApiModelProperty("函数参数说明")
        private String paramDesc;
        @ApiModelProperty("函数说明")
        private String description;
        @ApiModelProperty("使用示例")
        private String example;
    }

    /**
     * 函数类型
     */
    enum TypeEnum {
        /**
         * 已提供的函数类型
         */
        FUN_TYPE_CODEC(IFunc.NAME_SPACE_PREFIX + "codec", "编解码类",FunctionEnumSet.CodecEnum.values()),
        FUN_TYPE_COMMON(IFunc.NAME_SPACE_PREFIX + "common", "公共类",FunctionEnumSet.CommonEnum.values()),
        FUN_TYPE_DATE(IFunc.NAME_SPACE_PREFIX + "date", "日期类",FunctionEnumSet.DateEnum.values()),
        FUN_TYPE_LIST(IFunc.NAME_SPACE_PREFIX + "list", "列表类",FunctionEnumSet.ListEnum.values()),
        FUN_TYPE_MATH(IFunc.NAME_SPACE_PREFIX + "math", "数学类",FunctionEnumSet.MathEnum.values()),
        FUN_TYPE_STRING(IFunc.NAME_SPACE_PREFIX + "string", "字符串类",FunctionEnumSet.StringEnum.values()),
        FUN_TYPE_CUSTOM(IFunc.NAME_SPACE_PREFIX + "custom", "自定义函数",null);

        private String method;
        private String description;
        private FunctionEnumSet[] functionEnum;

        TypeEnum(String method, String description,FunctionEnumSet[] functionEnum) {
            this.method = method;
            this.description = description;
            this.functionEnum = functionEnum;
        }

        public String getMethod() {
            return method;
        }

        public String getDescription() {
            return description;
        }

        public FunctionEnumSet[] getFunctionEnum() {
            return functionEnum;
        }}

    /**
     * 编解码类
     */
    enum CodecEnum implements FunctionEnumSet {

        /**
         * 编解码类函数
         */
        CODEC_MD5(IFunc.NAME_SPACE_PREFIX + "codec.md5", "String", "(String data)", "data: 需进行加密的数据", "MD5加密", "如：\n" +
                "fn.codec.md5(\"abc\")\n" +
                "返回：\n" +
                "900150983cd24fb0d6963f7d28e17f72"),
        CODEC_SHA1(IFunc.NAME_SPACE_PREFIX + "codec.sha1", "String", "(String data)", "data: 需进行加密的数据", "SHA1加密", "如：\n" +
                "fn.codec.sha1(\"abc\")\n" +
                "返回：\n" +
                "a9993e364706816aba3e25717850c26c9cd0d89d"),
        CODEC_SHA256(IFunc.NAME_SPACE_PREFIX + "codec.sha256", "String", "(String data)", "data: 需进行加密的数据", "SHA256加密", "如：\n" +
                "fn.codec.sha256(\"abc\")"),
        CODEC_SHA384(IFunc.NAME_SPACE_PREFIX + "codec.sha384", "String", "(String data)", "data: 需进行加密的数据", "SHA384加密", "如：\n" +
                "fn.codec.sha384(\"abc\")"),
        CODEC_SHA512(IFunc.NAME_SPACE_PREFIX + "codec.sha512", "String", "(String data)", "data: 需进行加密的数据", "SHA512加密", "如：\n" +
                "fn.codec.sha512(\"abc\")"),
        CODEC_BASE64ENCODE(IFunc.NAME_SPACE_PREFIX + "codec.base64Encode", "String", "(String data)", "data: 需进行加密的数据", "base64加密", "如：\n" +
                "fn.codec.base64Encode(\n" +
                "\"Base64编码介绍\")\n" +
                "返回：\n" +
                "QmFzZTY057yW56CB5LuL57uN"),
        CODEC_BASE64DECODE(IFunc.NAME_SPACE_PREFIX + "codec.base64Decode", "String", "(String data)", "data: 需进行解密的数据", "base64解密", "如：\n" +
                "fn.codec.base64Decode(\n" +
                "\"QmFzZTY057yW56CB5LuL57uN\")\n" +
                "返回：\n" +
                "Base64编码介绍"),
        CODEC_AESENCRYPT(IFunc.NAME_SPACE_PREFIX + "codec.aesEncrypt", "String", "(String data, String key)", "data: 需进行加密的数据\n" +
                "key:密钥", "AES加密", "如：\n" +
                "fn.codec.aesEncrypt(\"abc\",\n" +
                "\"1234567812345678\")"),
        CODEC_AESDECRYPT(IFunc.NAME_SPACE_PREFIX + "codec.aesDecrypt", "String", "(String data, String key)", "data: 需进行解密的数据\n" +
                "key:密钥", "AES解密", "如：\n" +
                "fn.codec.aesDecrypt(\"abc\",\n" +
                "\"1234567812345678\")"),
        CODEC_DESENCRYPT(IFunc.NAME_SPACE_PREFIX + "codec.desEncrypt", "String", "(String data, String key)", "data: 需进行加密的数据\n" +
                "key:密钥", "DES加密", "如：\n" +
                "fn.codec.desEncrypt(\"abc\",\n" +
                "\"123456781234567812345\n" +
                "67812345678\")"),
        CODEC_DESDECRYPT(IFunc.NAME_SPACE_PREFIX + "codec.desDecrypt", "String", "(String data, String key)", "data: 需进行解密的数据\n" +
                "key:密钥", "DES解密", "如：\n" +
                "fn.codec.desDecrypt(\"abc\",\n" +
                "\"123456781234567812345\n" +
                "67812345678\")");

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        CodecEnum(String method, String returnType, String param, String paramDesc, String description, String example) {
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }

    /**
     * 公共类
     */
    enum CommonEnum implements FunctionEnumSet {

        /**
         * 公共函数枚举
         */
        COMMON_IIF(IFunc.NAME_SPACE_PREFIX + "common.iif", "Object", "(boolean exprResult,Object truepart,Object falsepart)", "exprResult：引用值或函数的返回值\n" +
                "truepart：第一个参数为true时的返回值\n" +
                "falsepart第一个参数为false时的返回值", "条件判断函数，当第一个参数\n" +
                "为true时返回第二个参数，否则\n" +
                "返回第三个", "fn.common.iif(true,1,2)返回1"),
        COMMON_EQUALS(IFunc.NAME_SPACE_PREFIX + "common.equals", "boolean", "(Object obj1, Object obj2)", "obj1: 对象1\n" +
                "obj2: 对象2", "判断两个对象是否相等", ""),
        COMMON_ISNULL(IFunc.NAME_SPACE_PREFIX + "common.isNull", "boolean", "(Object obj)", "obj: 对象", "判断对象是否null", ""),
        COMMON_ISNOTNULL(IFunc.NAME_SPACE_PREFIX + "common.isNotNull", "boolean", "(Object obj)", "obj: 对象", "判断对象是否不为null", ""),
        COMMON_ISBLANK(IFunc.NAME_SPACE_PREFIX + "common.isBlank", "boolean", "(String str)", "Str: 字符串", "判断字符串是否为空", ""),
        COMMON_ISNOTBLANK(IFunc.NAME_SPACE_PREFIX + "common.isNotBlank", "boolean", "(String str)", "Str: 字符串", "判断字符串是否不为空", ""),
        COMMON_ISEMPTY(IFunc.NAME_SPACE_PREFIX + "common.isEmpty", "boolean", "(Object obj)", "obj: 对象", "判断对象是否为空，主要用于\n" +
                "List/Map/数组/字符串类型的对象", ""),
        COMMON_ISNOTEMPTY(IFunc.NAME_SPACE_PREFIX + "common.isNotEmpty", "boolean", "(Object obj)", "obj: 对象", "判断对象是否不为空，主要用于\n" +
                "List/Map/数组/字符串类型的对象", "");

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        CommonEnum(String method, String returnType, String param, String paramDesc, String description, String example) {
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }

    /**
     * 日期类
     */
    enum DateEnum implements FunctionEnumSet {

        /**
         * 日期函数枚举
         */
        DATE_TIMESTAMP(IFunc.NAME_SPACE_PREFIX + "date.timestamp", "long", "()", "", "生成当前时间戳", ""),
        DATE_GETTIME(IFunc.NAME_SPACE_PREFIX + "date.getTime", "long", "(String date,String pattern,String timeZone)", "date:日期\n" +
                "pattern: 日期格式，\n" +
                "timeZone: 【可选】时区,默认东八区\n" +
                "如：GMT+08:00", "获取日期对应的时间戳", "fn.date.getTime(\"2021-08-04\",\"yyyy-MM-dd\")"),
        DATE_NOW(IFunc.NAME_SPACE_PREFIX + "date.now", "String", "(String pattern,String timeZone)", "pattern: 日期格式，\n" +
                "常用格式有：\n" +
                "yyyy-MM-dd HH:mm:ss\n" +
                "yyyy-MM-dd\n" +
                "HH:mm:ss\n" +
                "HH:mm\n" +
                "yyyy-MM-dd HH:mm:ss Z\n" +
                "timeZone 【可选】时区,默认东八区\n" +
                "如：GMT+08:00", "获取当前时间", "如获取当前日期:\n" +
                "fn.date.now(\"yyyy-MM-dd\")"),
        DATE_ADD(IFunc.NAME_SPACE_PREFIX + "date.add", "String", "(String date,String pattern,int field, int amount,String timeZone)", "date: 日期\n" +
                "pattern：date参数的日期格式\n" +
                "field：日期字段\n" +
                "1 for millisecond\n" +
                "2 for second\n" +
                "3 for minute\n" +
                "4 for hour\n" +
                "5 for date\n" +
                "6 for month\n" +
                "7 for year\n" +
                "amount: 时间数量\n" +
                "timeZone 【可选】时区,默认东八区\n" +
                "如：GMT+08:00", "给日期增加时间", "如给时间\n" +
                "\"2021-08-04 14:23:12\"加5小时：\n" +
                "fn.date.add(\"2021-08-04 14:23:12\",\n" +
                "\"yyyy-MM-dd HH:mm:ss\", 4, 5)\n" +
                "得到：2021-08-04 19:23:12"),
        DATE_FORMATTS(IFunc.NAME_SPACE_PREFIX + "date.formatTs", "String", "(long timestamp,String pattern,String timeZone)", "timestamp：时间戳\n" +
                "pattern：目标日期格式\n" +
                "timeZone 【可选】时区,默认东八区\n" +
                "如：GMT+08:00", "将时间戳格式化为日期", ""),
        DATE_CHANGEPATTERN(IFunc.NAME_SPACE_PREFIX + "date.changePattern", "String", "(String dateStr,String sourcePattern,String targetPattern,String timeZone)", "dateStr：日期\n" +
                "sourcePattern：dateStr参数的日期格式\n" +
                "targetPattern：目标日期格式\n" +
                "timeZone 【可选】时区,默认东八区\n" +
                "如：GMT+08:00", "将日期转换为另一种格式", "");

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        DateEnum(String method, String returnType, String param, String paramDesc, String description, String example) {
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }

    /**
     * 数据列表类
     */
    enum ListEnum implements FunctionEnumSet {

        /**
         * 数据列表函数枚举
         */
        LIST_EXPAND(IFunc.NAME_SPACE_PREFIX + "list.expand", "List<Object>", "(List<List<Object>> data)", "data: 二维数组（列表）", "将二维数组(列表)展开为一维数组(列表)", "如：\n" +
                "data=[[{a:1}],[{a:2}],[{a:3}]]\n" +
                "fn.list.expand(data)\n" +
                "返回：[{a:1},{a:2},{a:3}]"),
        LIST_MERGE(IFunc.NAME_SPACE_PREFIX + "list.merge", "List<Object>", "(List<Object>... data)", "data: 列表，可多个", "将多个列表合并成一个", "如：\n" +
                "data1=[{a:1}]\n" +
                "data2=[{a:2}]\n" +
                "data3=[{a:3}]\n" +
                "fn.list.merge(data1,data2,data3)\n" +
                "返回：[{a:1},{a:2},{a:3}]"),
        LIST_EXTRACT(IFunc.NAME_SPACE_PREFIX + "list.extract", "List<Map<String, Object>>", "(List<Map<String, Object>> data,String... fields)", "data: 列表\n" +
                "fields: 【可选】字段，可多个", "只提取列表里的部分字段", "如：\n" +
                "data=[\n" +
                "{a:1,b:4,c:7},\n" +
                "{a:2,b:5,c:8},\n" +
                "{a:3,b:6,c:9}\n" +
                "]\n" +
                "fn.list.extract(data, \"a\", \"c\")\n" +
                "返回：[\n" +
                "{a:1,c:7},\n" +
                "{a:2,c:8},\n" +
                "{a:3,c:9}\n" +
                "]"),
        LIST_JOIN(IFunc.NAME_SPACE_PREFIX + "list.join", "List<Map<String, Object>>", "(List<Map<String, Object>> dest,List<Map<String, Object>> src,String joinField,String... fields)", "dest: 目标列表\n" +
                "src: 被合并的列表\n" +
                "joinField: 两个列表关联的字段\n" +
                "格式：\n" +
                "dest列表的关联字段:src列表的\n" +
                "关联字段，如：userName:uname,\n" +
                "如果两个列表的关联字段一样可\n" +
                "只填一个，如：userName\n" +
                "fields: 【可选】被合并列表里需合并的字段，可多个，不填则合并所有字段", "合并2个列表字段，可只合并指定字段", "如：\n" +
                "dest=[\n" +
                "{a:1,b:4,c:7},\n" +
                "{a:2,b:5,c:8},\n" +
                "{a:3,b:6,c:9}\n" +
                "]\n" +
                "src=[\n" +
                "{a:1,d:444,e:777},\n" +
                "{a:2,d:555,e:888}\n" +
                "]\n" +
                "fn.list.join(dest,src, \"a\", \"d\")\n" +
                "返回：[\n" +
                "{a:1,b:4,c:7,d:444},\n" +
                "{a:2,b:5,c:8,d:555},\n" +
                "{a:3,b:6,c:9}\n" +
                "]"),
        LIST_RENAME(IFunc.NAME_SPACE_PREFIX + "list.rename", "List<Map<String, Object>>", "(List<Map<String, Object>> data,String... fieldPairs)", "data: 列表\n" +
                "fieldPairs: 【可选】需重命名\n" +
                "的字段对，可多个。格式：\n" +
                "原字段名:新字段名，如：c:cat", "重命名列表里的部分字段", "如：\n" +
                "data=[\n" +
                "{a:1,b:4,c:7},\n" +
                "{a:2,b:5,c:8},\n" +
                "{a:3,b:6,c:9}\n" +
                "]\n" +
                "fn.list.rename(data, \"a:apple\", \"c:cat\")\n" +
                "返回：[\n" +
                "{apple:1,b:4,cat:7},\n" +
                "{apple:2,b:5,cat:8},\n" +
                "{apple:3,b:6,cat:9}\n" +
                "]"),
        LIST_REMOVEFIELDS(IFunc.NAME_SPACE_PREFIX + "list.removeFields", "List<Map<String, Object>>", "(List<Map<String, Object>> data,String... fields)", "data: 列表\n" +
                "fields: 【可选】需删除的\n" +
                "字段，可多个", "删除列表里的部分字段", "如：\n" +
                "data=[\n" +
                "{a:1,b:4,c:7},\n" +
                "{a:2,b:5,c:8},\n" +
                "{a:3,b:6,c:9}\n" +
                "]\n" +
                "fn.list.removeFields(data, \"b\")\n" +
                "返回：[\n" +
                "{a:1,c:7},\n" +
                "{a:2,c:8},\n" +
                "{a:3,c:9}\n" +
                "]")
        ,
        LIST_CONVERTTOLIST(IFunc.NAME_SPACE_PREFIX + "list.convertToList", "List<Object>", "(Object... objects)", "objects: 多入参，类型需要一致", "将入参转换成List",
                "(Object a, Object b,....)")
        ,LIST_STRING_TO_LIST(IFunc.NAME_SPACE_PREFIX + "list.stringToList", "List<String>", "(String string,String splitRegex)", "string: 字符串\n" +
                "splitRegex：拆分字符或正则表达式\n", "将字符串转换成List",
                "");

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        ListEnum(String method, String returnType, String param, String paramDesc, String description, String example) {
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }

    /**
     * 数学类
     */
    enum MathEnum implements FunctionEnumSet {

        /**
         * 数学函数枚举
         */
        MATH_ABSEXACT(IFunc.NAME_SPACE_PREFIX + "math.absExact", "Long", "(long a)", "a: 整数", "获取绝对值", "如：\n" +
                "fn.math.absExact(-32)\n" +
                "返回：32\n" +
                "fn.math.absExact(5)\n" +
                "返回：5"),
        MATH_NEGATEEXACT(IFunc.NAME_SPACE_PREFIX + "math.negateExact", "Long", "(long a)", "a: 整数", "取反", "如：\n" +
                "fn.math.negateExact(-3)\n" +
                "返回：3\n" +
                "fn.math.negateExact(5)\n" +
                "返回：-5"),
        MATH_ADDEXACT(IFunc.NAME_SPACE_PREFIX + "math.addExact", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "两整数相加", "如：\n" +
                "fn.math.addExact(3,2)\n" +
                "返回：5"),
        MATH_SUBTRACTEXACT(IFunc.NAME_SPACE_PREFIX + "math.subtractExact", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "两整数相减", "如：\n" +
                "fn.math.subtractExact(3,2)\n" +
                "返回：1"),
        MATH_MULTIPLYEXACT(IFunc.NAME_SPACE_PREFIX + "math.multiplyExact", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "两整数相乘", "如：\n" +
                "fn.math.multiplyExact(3,2)\n" +
                "返回：6"),
        MATH_MAXEXACT(IFunc.NAME_SPACE_PREFIX + "math.maxExact", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "取两整数中的最大值", "如：\n" +
                "fn.math.maxExact(3,2)\n" +
                "返回：3"),
        MATH_MINEXACT(IFunc.NAME_SPACE_PREFIX + "math.minExact", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "取两整数中的最小值", "如：\n" +
                "fn.math.minExact(3,2)\n" +
                "返回：2"),
        MATH_MOD(IFunc.NAME_SPACE_PREFIX + "math.mod", "Long", "(long x, long y)", "x: 整数\n" +
                "y: 整数", "取模", "如：\n" +
                "fn.math.mod(3,2)\n" +
                "返回：1"),
        MATH_POW(IFunc.NAME_SPACE_PREFIX + "math.pow", "double", "(double a,double b)", "a:浮点数\n" +
                "b:浮点数", "乘方", "如：\n" +
                "fn.math.pow(3,2)\n" +
                "返回：9"),
        MATH_SQRT(IFunc.NAME_SPACE_PREFIX + "math.sqrt", "double", "(double a)", "a:浮点数", "开平方根", "如：\n" +
                "fn.math.sqrt(9)\n" +
                "返回：3"),
        MATH_RANDOM(IFunc.NAME_SPACE_PREFIX + "math.random", "double", "()", "", "获取随机数，返回一个0到1间的浮点数", ""),
        MATH_ABSDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.absDecimal", "double", "(double a)", "a:浮点数", "获取绝对值", "如：\n" +
                "fn.math.absDecimal(-3.2)\n" +
                "返回：3.2\n" +
                "fn.math.absDecimal(5.1)\n" +
                "返回：5.1"),
        MATH_NEGATEDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.negateDecimal", "double", "(double a)", "a:浮点数", "取反", "如：\n" +
                "fn.math.negateDecimal(-3.2)\n" +
                "返回：3.2\n" +
                "fn.math.negateDecimal(5.2)\n" +
                "返回：-5.2"),
        MATH_ADDDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.addDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "两浮点数相加", "如：\n" +
                "fn.math.addDecimal(3.1,2.2)\n" +
                "返回：5.3"),
        MATH_SUBTRACTDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.subtractDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "两浮点数相减", "如：\n" +
                "fn.math.subtractDecimal(3.1,2.2)\n" +
                "返回：0.9"),
        MATH_MULTIPLYDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.multiplyDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "两浮点数相乘", "如：\n" +
                "fn.math.multiplyDecimal(3.2,2)\n" +
                "返回：6.4"),
        MATH_DIVIDEDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.divideDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "两浮点数相除", "如：\n" +
                "fn.math.divideDecimal(4.2,2)\n" +
                "返回：2.1"),
        MATH_MAXDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.maxDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "取两整数中的最大值", "如：\n" +
                "fn.math.maxDecimal(3.2,2)\n" +
                "返回：3.2"),
        MATH_MINDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.minDecimal", "double", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "取两整数中的最小值", "如：\n" +
                "fn.math.minDecimal(3.2,2)\n" +
                "返回：2"),
        MATH_SCALEDECIMAL(IFunc.NAME_SPACE_PREFIX + "math.scaleDecimal", "double", "(double a, int scale)", "a:浮点数\n" +
                "scale:保留小数位数", "设置小数位数，四舍五入", "如：\n" +
                "fn.math.scaleDecimal(3.236787,2)\n" +
                "返回：3.24"),
        MATH_COMPARE(IFunc.NAME_SPACE_PREFIX + "math.compare", "int", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "比较x和y\n" +
                "x小于y返回-1,\n" +
                "x等于于y返回0,\n" +
                "x大于y返回1", ""),
        MATH_EQUALS(IFunc.NAME_SPACE_PREFIX + "math.equals", "Boolean", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "判断x是否等于y", ""),
        MATH_LT(IFunc.NAME_SPACE_PREFIX + "math.lt", "Boolean", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "判断x是否小于y", ""),
        MATH_LE(IFunc.NAME_SPACE_PREFIX + "math.le", "Boolean", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "判断x是否小于等于y", ""),
        MATH_GT(IFunc.NAME_SPACE_PREFIX + "math.gt", "Boolean", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "判断x是否大于y", ""),
        MATH_GE(IFunc.NAME_SPACE_PREFIX + "math.ge", "Boolean", "(double x, double y)", "x:浮点数\n" +
                "y:浮点数", "判断x是否大于等于y", "");

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        MathEnum(String method, String returnType, String param, String paramDesc, String description, String example) {
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }
    /**
     * 字符串类
     */
    enum StringEnum implements FunctionEnumSet{

        /**
         * 字符串函数枚举
         */
        STRING_EQUALS(IFunc.NAME_SPACE_PREFIX + "string.equals","Boolean","(String str1,String str2)","str1: 字符串\n" +
                "str2: 字符串","str1是否等于str2",""),
        STRING_EQUALSIGNORECASE(IFunc.NAME_SPACE_PREFIX + "string.equalsIgnoreCase","Boolean","(String str1,String str2)","str1: 字符串\n" +
                "str2: 字符串","str1是否等于(不区分大小写)str2",""),
        STRING_COMPARE(IFunc.NAME_SPACE_PREFIX + "string.compare","Int","(String str1,String str2)","str1: 字符串\n" +
                "str2: 字符串","比较str1和str2\n" +
                "str1小于str2返回-1,\n" +
                "str1等于于str2返回0,\n" +
                "str1大于str2返回1",""),
        STRING_CONCAT(IFunc.NAME_SPACE_PREFIX + "string.concat","String","(String... strs)","Strs: 字符串，可多个","拼接字符串","如：\n" +
                "fn.string.concat(\"a\",\"b\",\"c\")\n" +
                "返回：abc"),
        STRING_CONCATWS(IFunc.NAME_SPACE_PREFIX + "string.concatws","String","(String separator,String... strs)","separator：分隔符\n" +
                "Strs: 字符串，可多个","拼接字符串","如：\n" +
                "fn.string.concatws(\"-\",\"a\",\"b\",\"c\")\n" +
                "返回：a-b-c"),
        STRING_SUBSTRING(IFunc.NAME_SPACE_PREFIX + "string.substring","String","(String str,int beginIndex, int endIndex)","str:字符串\n" +
                "beginIndex:开始下标\n" +
                "endIndex:【可选】结束下标,不填或者填-2表示只从beginIndex开始下标截取到字符串最后","截取字符串","如：\n" +
                "fn.string.substring(\"abcd\",1,2)\n" +
                "返回：b\n" +
                "fn.string.substring(\"abcd\",1)\n" +
                "返回：bcd"),
        STRING_INDEXOF(IFunc.NAME_SPACE_PREFIX + "string.indexOf","Int","(String str,String substr)","str:字符串\n" +
                "substr:子字符串","返回子字符串的下标","如：\n" +
                "fn.string.indexOf(\"abcd\", \"bc\")\n" +
                "返回：1"),
        STRING_STARTSWITH(IFunc.NAME_SPACE_PREFIX + "string.startsWith","Boolean","(String str,String prefix)","str:字符串\n" +
                "prefix:前缀","字符串是否以指定前缀开头","如：\n" +
                "fn.string.startsWith(\"abcd\", \"bd\")\n" +
                "返回：false"),
        STRING_ENDSWITH(IFunc.NAME_SPACE_PREFIX + "string.endsWith","Boolean","(String str,String suffix)","str:字符串\n" +
                "suffix:后缀","字符串是否以指定后缀结尾","如：\n" +
                "fn.string.endsWith(\"abcd\", \"cd\")\n" +
                "返回：true"),
        STRING_TOUPPERCASE(IFunc.NAME_SPACE_PREFIX + "string.toUpperCase","String","(String str)","str:字符串","转换为大写","如：\n" +
                "fn.string.toUpperCase(\"abcd\")\n" +
                "返回：ABCD"),
        STRING_TOLOWERCASE(IFunc.NAME_SPACE_PREFIX + "string.toLowerCase","String","(String str)","str:字符串","转换为小写","如：\n" +
                "fn.string.toLowerCase(\"aBCd\")\n" +
                "返回：abcd"),
        STRING_UUID(IFunc.NAME_SPACE_PREFIX + "string.uuid","String","()","","获取UUID","如：\n" +
                "fn.string.uuid()"),
        STRING_TOSTRING(IFunc.NAME_SPACE_PREFIX + "string.toString","String","(Object obj)","obj:任意值","转换成字符串",""),
        STRING_REPLACE(IFunc.NAME_SPACE_PREFIX + "string.replace","String","(String str,String target,String replacement)","str:字符串\n" +
                "target:需被替换的字符串\n" +
                "replacement:替换的字符串","替换指定字符串","如：\n" +
                "fn.string.replace(\"abcd\", \"bc\",\"111\")\n" +
                "返回：a111d"),
        STRING_REPLACEALL(IFunc.NAME_SPACE_PREFIX + "string.replaceAll","String","(String str,String regex,String replacement)","str:字符串\n" +
                "regex:正则表达式用于匹配\n" +
                "需被替换的字符串\n" +
                "replacement:替换的字符串","替换正则表达式匹配到的字符串","如：\n" +
                "fn.string.replaceAll(\"abcbcd\", \"bc\",\"111\")\n" +
                "返回：a111111d"),
        STRING_REPLACEFIRST(IFunc.NAME_SPACE_PREFIX + "string.replaceFirst","String","(String str,String regex,String replacement)","str:字符串\n" +
                "regex:正则表达式用于匹配\n" +
                "需被替换的字符串\n" +
                "replacement:替换的字符串","替换正则表达式匹配到的第一个字符串","如：\n" +
                "fn.string.replaceFirst(\"abcbcd\", \"bc\",\"111\")\n" +
                "返回：a111bcd")
        ;

        private String method;

        private String returnType;

        private String param;

        private String paramDesc;

        private String description;

        private String example;

        StringEnum(String method,String returnType,String param,String paramDesc,String description,String example){
            this.method = method;
            this.returnType = returnType;
            this.param = param;
            this.paramDesc = paramDesc;
            this.description = description;
            this.example = example;
        }

        @Override
        public String method() {
            return this.method;
        }

        @Override
        public String returnType() {
            return this.returnType;
        }

        @Override
        public String param() {
            return this.param;
        }

        @Override
        public String paramDesc() {
            return this.paramDesc;
        }

        @Override
        public String description() {
            return this.description;
        }

        @Override
        public String example() {
            return this.example;
        }


    }



}
