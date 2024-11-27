package com.wakedata.dw.open.controller;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.accesstoken.AppAccessInfo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.openapi.OpenApiDataCache;
import com.wakedata.dw.open.openapi.OpenApiGatewayFactory;
import com.wakedata.dw.open.service.openapi.OpenApiParams;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author yiyufeng tanzhi
 * @title DataApiGatewayController
 * @projectName bdp-open
 * @date
 * @description
 */
@RequestMapping("${spring.mvc.backend.api.prefix}/{apiPrefix}")
@RestController
@Slf4j
@Api(value = "开放平台数据访问api", tags = "开放平台数据访问api")
public class DataApiGatewayController {

    @Resource
    private OpenApiDataCache openApiDataCache;

    @Resource
    private OpenApiGatewayFactory openApiGatewayFactory;

    @Value("${spring.mvc.backend.api.max-page-size}")
    private int maxPageSize = 1000;

    /**
     * 执行api
     *
     * @param request
     * @param response
     * @param apiPrefix
     * @param appKey
     * @param seqNo
     * @param groupPath
     * @param path1
     * @param path2
     * @param method
     * @param timestamp
     * @param version
     * @param sign
     * @param pageQuery
     * @param accessToken
     * @param postData
     * @param <T>
     * @return
     */
    @RequestMapping(value = {"/{groupPath}/{method}", "/{groupPath}/{path1}/{method}", "/{groupPath}/{path1}/{path2}/{method}"})
    public <T> T dataApiGateway(
            HttpServletRequest request,
            HttpServletResponse response,
            @PathVariable String apiPrefix,
            @RequestParam(required = false) String appKey,
            @RequestParam(required = false) String seqNo,
            @PathVariable String groupPath,
            @PathVariable(required = false) String path1,
            @PathVariable(required = false) String path2,
            @PathVariable String method,
            @RequestParam(value = "timestamp",required = false) String timestamp,
            @RequestParam(required = false) String version,
            @RequestParam(value = "sign",required = false) String sign,
            PageQuery pageQuery,
            @RequestParam(value = "accessToken",required = false) String accessToken,
            @RequestBody(required = false) String postData) {
        //设置默认分页参数
        pageQuery = ObjectUtil.defaultIfNull(pageQuery, new PageQuery(PageQuery.DEFAULT_PAGE_NO, maxPageSize));

        int pageSize = Math.min(pageQuery.getPageSize(), maxPageSize);
        log.info("api gateway invoke appKey {} accessToken {} seq {} method {} timestamp {} version {} sign {} pageNo {} pageSize {} postData {}",
                appKey, accessToken, seqNo, method, timestamp, version, sign, pageQuery.getPageNo(), pageSize, postData);
        OpenApiParams openApiParams = getOpenApiParams(request, response, appKey, seqNo, groupPath, path1, path2
                , method, timestamp, version, sign, pageQuery, postData);
        openApiParams.setMethod(openApiParams.getDataAssetApiMethod());
        DataAssetApiPo dataAssetApiPo = openApiDataCache.getDataAssetApiPo(openApiParams.getMethod());
        AppAccessInfo appAccessInfo = openApiDataCache.getAppAccessInfo(openApiParams.getAppKey());
        return (T) openApiGatewayFactory.getOpenApiGatewayService(dataAssetApiPo.getApiType())
                .invokeOpenApi(openApiParams, dataAssetApiPo, appAccessInfo);
    }

    private static OpenApiParams getOpenApiParams(HttpServletRequest request, HttpServletResponse response, String appId, String seqNo, String groupPath, String path1, String path2, String method, String timestamp, String version, String sign, PageQuery pageQuery, String postData) {
        OpenApiParams openApiParams = new OpenApiParams();
        openApiParams.setRequest(request);
        openApiParams.setResponse(response);
        openApiParams.setAppKey(appId);
        openApiParams.setSeqNo(seqNo);
        openApiParams.setGroupPath(groupPath);
        openApiParams.setPath1(path1);
        openApiParams.setPath2(path2);
        openApiParams.setMethod(method);
        openApiParams.setTimestamp(timestamp);
        openApiParams.setVersion(version);
        openApiParams.setSign(sign);
        openApiParams.setPageQuery(pageQuery);
        openApiParams.setPostData(postData);
        return openApiParams;
    }

    /**
     *   字段中如果有下列字符，请用右边的替换左边的请求参数
     *             空格 用%20代替
     *
     *              " 用%22代替
     *
     *              # 用%23代替
     *
     *             % 用%25代替
     *
     *             &用%26代替
     *
     *             ( 用%28代替
     *
     *             ) 用%29代替
     *
     *            + 用%2B代替
     *
     *             , 用%2C代替
     *
     *             / 用%2F代替
     *
     *             : 用%3A代替
     *
     *             ; 用%3B代替
     *
     *            < 用%3C代替
     *
     *            = 用%3D代替
     *
     *            > 用%3E代替
     *
     *            ? 用%3F代替
     *
     *            @ 用%40代替
     *
     *            \ 用%5C代替
     *
     *            | 用%7C代替
     */
}
