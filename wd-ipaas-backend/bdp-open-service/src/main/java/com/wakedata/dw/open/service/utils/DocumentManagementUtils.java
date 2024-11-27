package com.wakedata.dw.open.service.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.wakedata.common.core.dto.PageResultDTO;
import com.wakedata.common.core.exception.BizException;
import com.wakedata.common.core.hashids.HashidsUtil;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * xml格式起始标签和结束标签管理类
 *
 * @author zhengqinghui@wakedata.com
 * @date 2022/8/25 20:29
 */
@Slf4j
public class DocumentManagementUtils {

    /**
     * 标准化xml返回模版
     */
    private static final String STANDARD_XML_RESULT_TEMPLATE = "<xml><msg>%s</msg><code>%s</code><success>%s</success><data>%s</data></xml>";

    /**
     * 将来源的字符串加上<xml><xml/>
     *
     * @param str XML格式(字符串)
     * @return XML格式(字符串)
     */
    public static String addXmlLabel(String str) {
        return "<xml>" + str + "</xml>";
    }

    /**
     * 构建标准xml数据返回字符串
     *
     * @param msg     信息
     * @param code    状态码
     * @param success 成功状态
     * @param dataStr data字符串
     * @return xml数据返回字符串
     */
    public static String standardXmlResult(String msg, Integer code, Boolean success, String dataStr) {
        String retMsg = StringUtils.isBlank(msg) ? "" : msg;
        return String.format(STANDARD_XML_RESULT_TEMPLATE, retMsg, code, success, dataStr);
    }

    /**
     * 将来源的字符串去掉<xml><xml/>
     *
     * @param str XML格式(字符串)
     * @return XML格式(字符串)
     */
    public static String deleteXmlLabel(String str) {
        return str.replace("<xml>", "").replace("</xml>", "");
    }

    /**
     * 判断入参字符串是否是JSON格式，不是JSON格式的就返回标准化xml
     * 参数说明：
     *
     * @param data          需要转换的数据
     * @param exampleKey    json格式示例的key
     * @param exampleXmlKey xml格式示例的key
     * @param assetDatatype 入参数据的格式类型（json、xml）
     * @param exampleType   用于判断示例的类型（true->请求示例，false->响应示例）
     */
    public static Map<String, String> getJsonAndXml(String data, String exampleKey, String exampleXmlKey, String assetDatatype, Boolean exampleType) {
        String result = null;
        //将响应参数用wd-common中的PageResultDTO包裹，这样响应示例中就包含了公共响应参数
        PageResultDTO<Object> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount(10L);
        resultDTO.setPageSize(10);
        resultDTO.setPageNo(1);
        Map<String, String> map = new HashMap<>();
        //请求参数或者响应参数入参为空，返回固定值示例
        if (StringUtils.isBlank(data)) {
            return getResultMap(exampleType, exampleKey, exampleXmlKey, resultDTO);
        }
        JSONObject root = JSONUtil.parseObj(data).getJSONObject(JsonUtils.SCHEMA_ROOT);

        try {
            if (ObjectUtil.isEmpty(assetDatatype)) {
                result = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parseObj(data)));
                map.put(exampleKey, data);
                map.put(exampleXmlKey, result);
                return map;
            } else if (JsonUtils.SCHEMA_PARAMETER_OBJECT.equalsIgnoreCase((String) root.get(JsonUtils.SCHEMA_TYPE))) {
                result = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parseObj(JsonUtils.JsonSchemaToJson(data))));
                map.put(exampleKey, JsonUtils.JsonSchemaToJson(data));
                map.put(exampleXmlKey, result);
                return map;
            }else if (JsonUtils.SCHEMA_PARAMETER_ARRAY.equalsIgnoreCase((String)root.get(JsonUtils.SCHEMA_TYPE))) {
                result = DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parseArray(JsonUtils.JsonSchemaToJson(data))));
                map.put(exampleKey, JsonUtils.JsonSchemaToJson(data));
                map.put(exampleXmlKey, result);
                return map;
            } else {
                return getResultMap(exampleType, exampleKey, exampleXmlKey, resultDTO);
            }
        } catch (JSONException | OpenException e) {
            return getResultMap(exampleType, exampleKey, exampleXmlKey, resultDTO);
        }
    }

    /**
     * 当请求参数或者响应参数为空、数据转换格式发生异常时返回标准格式结果
     */
    public static Map<String, String> getResultMap(Boolean exampleType, String exampleKey, String exampleXmlKey, PageResultDTO<Object> pageResultDTO) {
        Map<String, String> map = new HashMap<>();
        if (exampleType) {
            map.put(exampleKey, "{}");
            map.put(exampleXmlKey, DocumentManagementUtils.addXmlLabel(" "));
            return map;
        }
        pageResultDTO.setData(" ");
        map.put(exampleKey, JSONUtil.toJsonPrettyStr(pageResultDTO));
        map.put(exampleXmlKey, DocumentManagementUtils.addXmlLabel(JSONUtil.toXmlStr(JSONUtil.parseObj(pageResultDTO))));
        return map;
    }

    /**
     * API的Id解码方法
     */
    public static Integer decode(String dataAssetApiId) {
        Integer apiId = null;
        try {
            //捕获解码异常
            apiId = (int) HashidsUtil.decodeDefault(dataAssetApiId);
        } catch (BizException e) {
            throw new OpenException(MsgCodeEnum.w_document_api_id_decoding_exception);
        }
        return apiId;
    }
}
