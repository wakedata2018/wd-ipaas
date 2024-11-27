package com.wakedata.dw.open.service.openapi;

import cn.hutool.core.util.ObjectUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.model.query.PageQuery;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author luomeng
 * @Description 开放平台对外api参数列表
 * @createTime 2022-08-07 18:11:00
 */
@Data
public class OpenApiParams {

    private HttpServletRequest request;
    private HttpServletResponse response;
    /**
     * api前缀
     */
    private String apiPrefix;
    /**
     * 应用appKey
     */
    private String appKey;
    /**
     * 流水号
     */
    private String seqNo;
    /**
     * 分组路径
     */
    private String groupPath;
    /**
     * url path1
     */
    private String path1;
    /**
     * url path2
     */
    private String path2;
    /**
     * url 末尾路径
     */
    private String method;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 版本号
     */
    private String version;
    /**
     * 签名
     */
    private String sign;
    /**
     * 分页参数
     */
    private PageQuery pageQuery;
    /**
     * token
     */
    private String accessToken;
    /**
     * body参数
     */
    private String postData;

    /**
     * 调用来源
     */
    private String apiInvokeSource;

    /**
     * api测试
     */
    private Boolean isApiTest;

    /**
     * 数据初始化
     * @param args
     */
    public void initParams(Object[] args){
        this.request = (HttpServletRequest) args[0];
        this.response = (HttpServletResponse) args[1];
        this.apiPrefix = (String) args[2];
        this.appKey = (String) args[3];
        this.seqNo = (String) args[4];
        this.groupPath = (String) args[5];
        this.path1 = (String) args[6];
        this.path2 = (String) args[7];
        this.method = (String) args[8];
        this.timestamp = (String) args[9];
        this.version = (String) args[10];
        this.sign = (String) args[11];
        this.pageQuery = (PageQuery) args[12];
        this.accessToken = (String) args[13];
        this.postData = (String) args[14];
        if(DwOpenConstant.OPEN_API_TEST_PREFIX.equals(this.apiPrefix)){
            this.isApiTest = Boolean.TRUE;
        }
    }


    /**
     * 获取请求的完整url
     * @return
     */
    public String getDataAssetApiMethod() {
        String groupApi = this.groupPath;
        if (ObjectUtil.isNotEmpty(this.path1)) {
            groupApi = groupApi + "/" + this.path1;
        }
        if (ObjectUtil.isNotEmpty(this.path2)) {
            groupApi = groupApi + "/" + this.path2;
        }
        groupApi = groupApi + "/" + this.method;
        return groupApi;
    }
}
