package com.wakedata.openapi.sdk;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.wakedata.openapi.sdk.common.OpenApiConstant;
import com.wakedata.openapi.sdk.exception.OpenApiException;
import com.wakedata.openapi.sdk.generator.ApiGroupDataConvertUtil;
import com.wakedata.openapi.sdk.generator.CodeGenerator;
import com.wakedata.openapi.sdk.generator.dto.ApiGroupDTO;
import com.wakedata.openapi.sdk.generator.param.ApiGroup;
import com.wakedata.openapi.sdk.generator.param.TemplateParam;
import com.wakedata.openapi.sdk.result.OpenApiResultDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载api
 * @author luomeng
 * @date 2022/10/28 10:39
 */
@Slf4j
public class CodeGeneratorApiTest {

    public static void main(String[] args) {
        //生成目录
        String generatorDir = "D:\\sdk-generator\\";
        //模板参数
        TemplateParam param = new TemplateParam();
        param.setProjectName("ipaas-prod");
        param.setProjectVersion("R2.0.0-S15-RC02");
        param.setAppKey(IdUtil.fastSimpleUUID());
        param.setAppSecret(IdUtil.fastSimpleUUID());
        param.setApiRequestHost("https://ipaas.wakedt.cn");
        //获取api需要使用平台管理员的cookie信息
        String cookieId = "ipaasJsid=7e919cf547ed449e87327c86289bdd71;";
        Integer testApiGroupId = null;
        param.setApiGroupList(getApiGroupList(param.getApiRequestHost(),cookieId,testApiGroupId));
        log.info("apiGroupList:{}",JSONUtil.toJsonStr(param.getApiGroupList()));
        //生成sdk
        File file = CodeGenerator.startGenerator(generatorDir,param);
        log.info("生成sdk地址：{}",file.getPath());

    }

    /**
     * 获取api并组装成模板需要的参数
     * @param apiRequestHost 请求域名
     * @param cookieId cookie信息
     * @param testApiGroupId 测试分组id
     * @return
     */
    private static List<ApiGroup> getApiGroupList(String apiRequestHost,String cookieId,Integer testApiGroupId) {
        Map<String,Object> params = new HashMap<>();
        if(testApiGroupId != null){
            params.put("testApiGroupId",testApiGroupId);
        }
        String api = String.join(OpenApiConstant.URL_JOIN_QUESTION, apiRequestHost + "/dw/open/business/getSdkApi", HttpUtil.toParams(params));
        String result = HttpRequest.get(api).header(Header.COOKIE,cookieId).execute().body();
        log.info("api result:\n{}", result);
        OpenApiResultDTO apiGroupDTO = JSONUtil.toBean(result, OpenApiResultDTO.class);
        if(!apiGroupDTO.getSuccess()){
            throw new OpenApiException(500,result);
        }
        return ApiGroupDataConvertUtil.convert(JSONUtil.toList(JSONUtil.toJsonStr(apiGroupDTO.getData()),ApiGroupDTO.class));
    }
}
