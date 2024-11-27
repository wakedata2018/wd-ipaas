package com.wakedata.dw.open.service.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.wakedata.dw.open.enums.DataAssetEnums;
import com.wakedata.dw.open.markdown.Markdown;
import com.wakedata.dw.open.markdown.constant.CodeLanguageEnum;
import com.wakedata.dw.open.markdown.constant.TitleLevelEnum;
import com.wakedata.dw.open.markdown.element.CodeElement;
import com.wakedata.dw.open.markdown.element.StringElement;
import com.wakedata.dw.open.markdown.element.TableElement;
import com.wakedata.dw.open.markdown.element.TitleElement;
import com.wakedata.dw.open.parammapping.HttpParamKind;
import com.wakedata.dw.open.service.api.dto.ApiRespParamDTO;
import com.wakedata.dw.open.service.vo.DocumentApiConditionVo;
import com.wakedata.dw.open.service.vo.DocumentApiDetailVo;
import groovy.xml.XmlUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Wangchensheng@wakedata.com
 * date 2023年03月09日 19:40:37
 */
public class DocumentMarkDownUtil {

    private static final String STRING_BANK = "";
    private static final String STRING_ENTER = "\n";
    private static final String STRING_DOUBLE_ENTER = "\n\n";
    private static final String STRING_MARK_DOWN_BANK = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
    private static final String STRING_POINT = ".";
    public static final String STRING_ESCAPE_SYMBOL = "<";
    public static final String STRING_ESCAPE = "\\";

    private static final String MARK_DOWN_FILE_TOC = "[TOC]";
    private static final String PRE_API_NAME = "接口基本介绍：";
    private static final String DEFAULT_API_DESC = "暂无描述";
    private static final String DEFAULT_TABLE_ROW = "暂无数据";
    private static final String DEFAULT_JSON_SIMPLE = "{}";
    private static final String DEFAULT_XML_SIMPLE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><xml/>";

    private static final String COMMON_PARAMS_TITLE = "公共参数";
    private static final String COMMON_REQUEST_ADDRESS = "请求地址：";
    private static final String COMMON_ENV = "环境";
    private static final String COMMON_REQUEST_PROTOCOL = "请求方式";
    private static final String COMMON_REQUEST_HTTPS_ADDRESS = "HTTPS地址";
    private static final String COMMON_ENV_VALUE = "正式环境";

    private static final String COMMON_REQUEST_PARAMS = "公共请求参数：";
    private static final String COMMON_REQUEST_TABLE_COLUM_NAME = "参数名称";
    private static final String COMMON_REQUEST_TABLE_COLUM_TYPE = "参数类型";
    private static final String COMMON_REQUEST_TABLE_REQUEST_TYPE = "请求类型";
    private static final String COMMON_REQUEST_TABLE_COLUM_IS_REQUIRED = "是否必须";
    private static final String COMMON_REQUEST_TABLE_COLUM_DESC = "参数说明";

    private static final String REQUEST_PARAMS_TITLE = "请求参数";
    private static final String REQUEST_PARAM_NAME = "参数名称";
    private static final String REQUEST_PARAM_TYPE = "类型";
    private static final String REQUEST_PARAM_IS_REQUIRED = "是否必须";
    private static final String REQUEST_PARAM_SIMPLE = "示例值";
    private static final String REQUEST_PARAM_DESC = "描述";
    private static final String REQUEST_PARAM_KIND = "请求类型";

    private static final String RESPONSE_PARAMS_TITLE = "响应参数";
    private static final String RESPONSE_PARAMS_NAME = "参数";
    private static final String RESPONSE_PARAMS_TYPE = "类型";
    private static final String RESPONSE_PARAMS_DESC = "描述";

    private static final String REQUEST_SIMPLE_TITLE = "请求示例";
    private static final String REQUEST_EXAMPLE_MAP_KEY = "requestExample";
    private static final String REQUEST_EXAMPLE_MAP_KEY_XML = "requestExampleXml";

    private static final String RESPONSE_SIMPLE_TITLE = "响应示例";
    private static final String RESPONSE_EXAMPLE_MAP_KEY = "responseExample";
    private static final String RESPONSE_EXAMPLE_MAP_KEY_XML = "responseExampleXml";

    private static final String EXCEPTION_SIMPLE_TITLE = "异常示例";
    private static final String ERROR_EXAMPLE_MAP_KEY = "errorExampleJson";
    private static final String ERROR_EXAMPLE_MAP_KEY_XML = "errorExampleXml";
    private static final String SIMPLE_TYPE_JSON = "【JSON】";
    private static final String SIMPLE_TYPE_XML = "【XML】";

    private static final String IS_REQUIRED = "是";
    private static final String NOT_REQUIRED = "否";

    /**
     * 构建接口分组名称
     *
     * @param apiGroupName 接口分组名称
     * @param markdown     markdown
     */
    public static void buildApiGroup(String apiGroupName, Markdown markdown) {
        TitleElement apiName = TitleElement.builder()
                .level(TitleLevelEnum.FIRST).content(apiGroupName.concat(STRING_ENTER))
                .build();
        markdown.add(apiName);
    }

    /**
     * 构建单条API的markDown的文本
     *
     * @param documentApiDetailVo documentApiDetailVo
     * @param markdown            markdown
     */
    public static void buildApiDetail(DocumentApiDetailVo documentApiDetailVo, Markdown markdown) {
        buildApiName(documentApiDetailVo.getDataAssetApi().getApiName(), markdown);
        buildApiDesc(documentApiDetailVo.getDataAssetApi().getApiDescription(), markdown);
        buildUrl(documentApiDetailVo.getDataAssetApi().getReqMethod().getValue(), documentApiDetailVo.getDomainName().concat(documentApiDetailVo.getDataAssetApi().getDataAssetApiMethod()), markdown);
        buildCommonRequestParam(markdown);
        buildCommonResponse(markdown);
        buildRequestBodyParam(getApiRequest(documentApiDetailVo), markdown);
        buildResponseBodyParam(getApiResponse(documentApiDetailVo), markdown);
        buildSimple(markdown, REQUEST_SIMPLE_TITLE,
                Optional.ofNullable(documentApiDetailVo.getParametersExample()).map(a -> a.get(REQUEST_EXAMPLE_MAP_KEY)).orElse(null),
                Optional.ofNullable(documentApiDetailVo.getParametersExample()).map(a -> a.get(REQUEST_EXAMPLE_MAP_KEY_XML)).orElse(null),
                documentApiDetailVo.getDataAssetApi().getReqMethod());
        buildSimple(markdown, RESPONSE_SIMPLE_TITLE,
                Optional.ofNullable(documentApiDetailVo.getResponseParamsExample()).map(a -> a.get(RESPONSE_EXAMPLE_MAP_KEY)).orElse(null),
                Optional.ofNullable(documentApiDetailVo.getResponseParamsExample()).map(a -> a.get(RESPONSE_EXAMPLE_MAP_KEY_XML)).orElse(null),
                null);
        buildSimple(markdown, EXCEPTION_SIMPLE_TITLE, Optional.ofNullable(documentApiDetailVo.getErrorExample()).map(a -> a.get(ERROR_EXAMPLE_MAP_KEY)).orElse(null),
                Optional.ofNullable(documentApiDetailVo.getErrorExample()).map(a -> a.get(ERROR_EXAMPLE_MAP_KEY_XML)).orElse(null),
                null);
    }

    /**
     * 构建文件目录
     *
     * @param markdown markdown
     */
    public static void buildDirectory(Markdown markdown) {
        // 文件目录
        StringElement directoryName = StringElement.builder()
                .content(MARK_DOWN_FILE_TOC.concat(STRING_ENTER)).build();
        markdown.add(directoryName);
    }

    /**
     * 构建接口名称
     *
     * @param apiNameContent 接口名称
     * @param markdown       markdown
     */
    private static void buildApiName(String apiNameContent, Markdown markdown) {
        // API名称
        TitleElement apiName = TitleElement.builder()
                .level(TitleLevelEnum.SECOND).content(apiNameContent.concat(STRING_ENTER))
                .build();
        markdown.add(apiName);
    }

    /**
     * 构建接口描述
     *
     * @param apiDescContent 接口描述
     * @param markdown       markdown
     */
    private static void buildApiDesc(String apiDescContent, Markdown markdown) {
        if (StringUtils.isBlank(apiDescContent)) {
            apiDescContent = DEFAULT_API_DESC;
        }

        // API描述
        StringElement apiDesc = StringElement.builder()
                .content(PRE_API_NAME.concat(apiDescContent).concat(STRING_DOUBLE_ENTER))
                .build();
        markdown.add(apiDesc);
    }

    /**
     * 构建请求地址
     *
     * @param reqMethod 请求方式
     * @param httpUrl   请求路径
     * @param markdown  markdown
     */
    private static void buildUrl(String reqMethod, String httpUrl, Markdown markdown) {
        TitleElement apiCommonParams = TitleElement.builder()
                .level(TitleLevelEnum.THIRD).content(COMMON_PARAMS_TITLE.concat(STRING_ENTER))
                .build();
        markdown.add(apiCommonParams);

        StringElement requestUrl = StringElement.builder()
                .content(COMMON_REQUEST_ADDRESS.concat(STRING_DOUBLE_ENTER))
                .build();
        markdown.add(requestUrl);

        List<TableElement.TableRow> requestUrlRow = new ArrayList<>();
        buildTableRow(Arrays.asList(COMMON_ENV_VALUE, reqMethod, httpUrl), requestUrlRow);
        TableElement tableElement = TableElement.builder()
                .titles(Arrays.asList(COMMON_ENV, COMMON_REQUEST_PROTOCOL, COMMON_REQUEST_HTTPS_ADDRESS))
                .rows(requestUrlRow)
                .build();
        markdown.add(tableElement);
    }

    /**
     * 构建公共请求参数
     *
     * @param markdown markdown
     */
    private static void buildCommonRequestParam(Markdown markdown) {
        StringElement commonRequestParams = StringElement.builder()
                .content(STRING_DOUBLE_ENTER.concat(COMMON_REQUEST_PARAMS).concat(STRING_DOUBLE_ENTER))
                .build();
        List<TableElement.TableRow> apiCommonParamRow = new ArrayList<>();
        buildTableRow(Arrays.asList("timestamp", "string", "QUERY", "是", "请求时间戳"), apiCommonParamRow);
        buildTableRow(Arrays.asList("accessToken", "string", "QUERY", "是", "accessToken"), apiCommonParamRow);
        buildTableRow(Arrays.asList("sign", "string", "QUERY", "是", "签名，使用api测试功能时可忽略"), apiCommonParamRow);
        TableElement commonRequestParamsTableElement = TableElement.builder()
                .titles(Arrays.asList(COMMON_REQUEST_TABLE_COLUM_NAME, COMMON_REQUEST_TABLE_COLUM_TYPE, COMMON_REQUEST_TABLE_REQUEST_TYPE, COMMON_REQUEST_TABLE_COLUM_IS_REQUIRED, COMMON_REQUEST_TABLE_COLUM_DESC))
                .rows(apiCommonParamRow)
                .build();
        markdown.add(commonRequestParams);
        markdown.add(commonRequestParamsTableElement);
    }

    /**
     * 构建公共响应参数
     *
     * @param markdown markdown
     */
    private static void buildCommonResponse(Markdown markdown) {

        StringElement commonRequestParams = StringElement.builder()
                .content(STRING_DOUBLE_ENTER.concat("公共响应参数").concat(STRING_DOUBLE_ENTER))
                .build();
        List<TableElement.TableRow> apiCommonParamRow = new ArrayList<>();
        buildTableRow(Arrays.asList("success", "boolean", "接口执行状态"), apiCommonParamRow);
        buildTableRow(Arrays.asList("data", "object", "返回的数据对象"), apiCommonParamRow);
        buildTableRow(Arrays.asList("code", "integer", "状态码"), apiCommonParamRow);
        buildTableRow(Arrays.asList("msg", "string", "返回内容的描述"), apiCommonParamRow);
        buildTableRow(Arrays.asList("pageSize", "integer", "分页参数，每页显示数据的条数"), apiCommonParamRow);
        buildTableRow(Arrays.asList("pageNo", "integer", "分页参数，当前页码"), apiCommonParamRow);
        buildTableRow(Arrays.asList("totalCount", "long", "分页参数，返回数据总条数"), apiCommonParamRow);
        TableElement commonRequestParamsTableElement = TableElement.builder()
                .titles(Arrays.asList("参数名称", "类型", "参数说明"))
                .rows(apiCommonParamRow)
                .build();
        markdown.add(commonRequestParams);
        markdown.add(commonRequestParamsTableElement);
    }

    /**
     * 构建请求参数表格
     *
     * @param apiConditionVoList 请求参数
     * @param markdown           markdown
     */
    private static void buildRequestBodyParam(List<DocumentApiConditionVo> apiConditionVoList, Markdown markdown) {
        buildTableRequest(markdown, apiConditionVoList,
                REQUEST_PARAM_NAME, REQUEST_PARAM_TYPE, REQUEST_PARAM_KIND, REQUEST_PARAM_IS_REQUIRED, REQUEST_PARAM_SIMPLE, REQUEST_PARAM_DESC);
    }

    /**
     * 构建响应参数表格
     *
     * @param apiConditionVoList 响应参数列表
     * @param markdown           markdown
     */
    private static void buildResponseBodyParam(List<ApiRespParamDTO> apiConditionVoList, Markdown markdown) {
        buildTableResponse(markdown, apiConditionVoList,
                RESPONSE_PARAMS_NAME, RESPONSE_PARAMS_TYPE, RESPONSE_PARAMS_DESC);
    }

    /**
     * 构建示例值
     *
     * @param markdown   markdown
     * @param content    示例值
     * @param jsonSimple 示例内容
     * @param reqMethod  请求方式
     */
    private static void buildSimple(Markdown markdown, String content, String jsonSimple, String xmlSimple, DataAssetEnums.ReqMethod reqMethod) {
        if (Objects.equals(REQUEST_SIMPLE_TITLE, content) && Objects.equals(DataAssetEnums.ReqMethod.GET, reqMethod)) {
            return;
        }

        TitleElement requestSimple = TitleElement.builder()
                .level(TitleLevelEnum.THIRD).content(content.concat(STRING_ENTER))
                .build();

        StringElement jsonCodeName = StringElement.builder()
                .content(STRING_DOUBLE_ENTER + content.concat(SIMPLE_TYPE_JSON) + STRING_DOUBLE_ENTER)
                .build();
        CodeElement jsonCode = CodeElement.builder()
                .language(CodeLanguageEnum.JSON.getDesc())
                .content(StringUtils.isBlank(jsonSimple) ? DEFAULT_JSON_SIMPLE : toPrettyFormatJson(jsonSimple))
                .build();

        StringElement xmlCodeName = StringElement.builder()
                .content(STRING_DOUBLE_ENTER + content.concat(SIMPLE_TYPE_XML) + STRING_DOUBLE_ENTER)
                .build();
        CodeElement xmlCode = CodeElement.builder()
                .language(CodeLanguageEnum.XML.getDesc())
                .content(StringUtils.isBlank(xmlSimple) ? DEFAULT_XML_SIMPLE : toPrettyFormatXml(xmlSimple))
                .build();
        markdown.add(requestSimple);
        markdown.add(jsonCodeName);
        markdown.add(jsonCode);
        markdown.add(xmlCodeName);
        markdown.add(xmlCode);
    }

    /**
     * 返回请求参数
     */
    private static List<DocumentApiConditionVo> getApiRequest(DocumentApiDetailVo documentApiDetailVo) {
        List<DocumentApiConditionVo> parameters = documentApiDetailVo.getParameters();
        return CollectionUtil.isEmpty(parameters) ? new ArrayList<>(1) : parameters;
    }

    /**
     * 根据类型返回的响应参数
     */
    private static List<ApiRespParamDTO> getApiResponse(DocumentApiDetailVo documentApiDetailVo) {
        List<ApiRespParamDTO> apiRespParamDtoList =
                Objects.equals(documentApiDetailVo.getDataAssetApi().getApiType(), DataAssetEnums.DataApiType.LITE_FLOW)
                        ? documentApiDetailVo.getResutRespParamDTOS()
                        : documentApiDetailVo.getResponseParams();
        return CollectionUtil.isEmpty(apiRespParamDtoList) ? new ArrayList<>(1) : apiRespParamDtoList;
    }


    /**
     * 格式化xml
     */
    private static String toPrettyFormatXml(String xml) {
        return StringUtils.isBlank(xml) ? "" : XmlUtil.serialize(xml);
    }

    /**
     * 格式化json
     */
    private static String toPrettyFormatJson(String json) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (StringUtils.isBlank(json)) {
            return "";
        }

        JsonParser jsonParser = new JsonParser();
        JsonElement parse = jsonParser.parse(json);
        return parse.isJsonArray() ? gson.toJson(parse.getAsJsonArray()) : gson.toJson(parse.getAsJsonObject());
    }


    /**
     * 解析请求表格
     */
    private static void buildTableRequest(Markdown markdown, List<DocumentApiConditionVo> apiConditionVoList, String... str) {
        List<TableElement.TableRow> tableRows = new ArrayList<>();

        TitleElement tableTitle = TitleElement.builder()
                .level(TitleLevelEnum.THIRD).content(REQUEST_PARAMS_TITLE.concat(STRING_ENTER))
                .build();

        for (DocumentApiConditionVo param : apiConditionVoList) {
            if (Objects.equals(param.getHttpParamKind(), HttpParamKind.HEAD)) {
                buildTableRow(Arrays.asList(param.getAssetColumns(), param.getAssetDatatype(), HttpParamKind.HEAD.name(), param.getRequired() ? IS_REQUIRED : NOT_REQUIRED, param.getSample(), param.getDescriptions()), tableRows);
            } else if (Objects.equals(param.getHttpParamKind(), HttpParamKind.QUERY)) {
                buildTableRow(Arrays.asList(param.getAssetColumns(), param.getAssetDatatype(), HttpParamKind.QUERY.name(), param.getRequired() ? IS_REQUIRED : NOT_REQUIRED, param.getSample(), param.getDescriptions()), tableRows);
            } else if (Objects.equals(param.getHttpParamKind(), HttpParamKind.BODY)) {
                TableElement.TableRow bodyRow = new TableElement.TableRow();
                ApiRespParamDTO body = JsonSchemaConvertUtil.convert(param.getJsonSchema());
                bodyRow.setRows(Arrays.asList(param.getAssetColumns(), getParamType(param.getAssetDatatype()), HttpParamKind.BODY.name(), param.getRequired() ? IS_REQUIRED : NOT_REQUIRED, null, param.getDescriptions()));
                tableRows.add(bodyRow);

                List<String> requiredList = JsonUtils.JsonSchemaToRequiredColum(param.getJsonSchema());
                parseBody(tableRows, body.getChildApiRespParams(), STRING_BANK, requiredList, 0, Boolean.FALSE);
            }
        }

        if (CollectionUtils.isEmpty(tableRows)) {
            buildTableRow(Collections.singletonList(DEFAULT_TABLE_ROW), tableRows);
        }
        TableElement tableElement = TableElement.builder()
                .titles(Arrays.asList(str))
                .rows(tableRows)
                .build();

        markdown.add(tableTitle);
        markdown.add(tableElement);
    }

    /**
     * 处理array<类型> 在markdown中转义不成功的问题
     */
    private static String getParamType(String paramType) {
        if (StringUtils.contains(paramType, STRING_ESCAPE_SYMBOL)) {
            StringBuilder sb = new StringBuilder(paramType);
            sb.insert(StringUtils.indexOf(paramType, STRING_ESCAPE_SYMBOL), STRING_ESCAPE);
            return sb.toString();
        }
        return paramType;
    }

    /**
     * 解析响应表格
     */
    private static void buildTableResponse(Markdown markdown, List<ApiRespParamDTO> apiConditionVoList, String... str) {
        List<TableElement.TableRow> tableRows = new ArrayList<>();

        TitleElement tableTitle = TitleElement.builder()
                .level(TitleLevelEnum.THIRD).content(RESPONSE_PARAMS_TITLE.concat(STRING_ENTER))
                .build();

        for (ApiRespParamDTO param : apiConditionVoList) {
            if (Objects.equals(param.getType(), HttpParamKind.HEAD)) {
                buildTableRow(Arrays.asList(param.getAssetColumns(), param.getType().name(), HttpParamKind.HEAD.name(), param.getAssetColumns()), tableRows);
            } else if (Objects.equals(param.getType(), HttpParamKind.QUERY)) {
                buildTableRow(Arrays.asList(param.getAssetColumns(), param.getType().name(), HttpParamKind.QUERY.name(), param.getAssetColumns()), tableRows);
            } else if (Objects.equals(param.getType(), HttpParamKind.BODY)) {
                TableElement.TableRow bodyRow = new TableElement.TableRow();
                ApiRespParamDTO body = JsonSchemaConvertUtil.convert(param.getResponsePostData());
                bodyRow.setRows(Arrays.asList(body.getAssetColumns(), getParamType(body.getAssetDataType()), HttpParamKind.BODY.name(), String.valueOf(Boolean.TRUE), null, body.getAssetColumns()));
                tableRows.add(bodyRow);

                List<String> requiredList = JsonUtils.JsonSchemaToRequiredColum(param.getResponsePostData());
                parseBody(tableRows, body.getChildApiRespParams(), "", requiredList, 0, Boolean.TRUE);
            }
        }

        if (CollectionUtils.isEmpty(tableRows)) {
            buildTableRow(Collections.singletonList(DEFAULT_TABLE_ROW), tableRows);
        }
        TableElement tableElement = TableElement.builder()
                .titles(Arrays.asList(str))
                .rows(tableRows)
                .build();

        markdown.add(tableTitle);
        markdown.add(tableElement);
    }

    /**
     * 递归解析表格
     */
    private static void parseBody(List<TableElement.TableRow> requestUrlRow, List<ApiRespParamDTO> childApiRespParams, String bank, List<String> requiredList, Integer level, Boolean isResponse) {
        List<String> required = isRequired(requiredList, level);
        bank = bank.concat(STRING_MARK_DOWN_BANK);
        for (ApiRespParamDTO apiRespParamDTO : childApiRespParams) {
            buildTableRow(buildTableValue(apiRespParamDTO, bank, isResponse, required), requestUrlRow);
            if (!CollectionUtils.isEmpty(apiRespParamDTO.getChildApiRespParams())) {
                parseBody(requestUrlRow, apiRespParamDTO.getChildApiRespParams(), bank, requiredList, ++level, isResponse);
            }
        }
    }

    /**
     * 根据level获取当前层级的必填字段
     */
    private static List<String> isRequired(List<String> requiredList, Integer level) {
        return requiredList.stream().filter(colum -> StringUtils.countMatches(colum, STRING_POINT) == level).collect(Collectors.toList());
    }

    private static List<String> buildTableValue(ApiRespParamDTO apiRespParamDTO, String bank, Boolean isResponse, List<String> required) {
        return isResponse ? Arrays.asList(bank + apiRespParamDTO.getAssetColumns(), getParamType(apiRespParamDTO.getAssetDataType()), apiRespParamDTO.getDescription())
                : Arrays.asList(
                bank + apiRespParamDTO.getAssetColumns(),
                getParamType(apiRespParamDTO.getAssetDataType()),
                null,
                required.stream().anyMatch(s -> StringUtils.endsWith(s, apiRespParamDTO.getAssetColumns())) ? IS_REQUIRED : NOT_REQUIRED,
                apiRespParamDTO.getDefaultValue(),
                apiRespParamDTO.getDescription());
    }

    /**
     * 构建表格中的行数据
     */
    private static void buildTableRow(List<String> rows, List<TableElement.TableRow> apiCommonParamRow) {
        TableElement.TableRow apiCommonParamTableRow1 = new TableElement.TableRow();
        apiCommonParamTableRow1.setRows(rows);
        apiCommonParamRow.add(apiCommonParamTableRow1);
    }

}
