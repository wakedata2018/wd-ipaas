package com.wakedata.dw.open.controller;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.UserPageActionPo;
import com.wakedata.dw.open.model.query.UserActionPageQuery;
import com.wakedata.dw.open.service.impl.CacheService;
import com.wakedata.dw.open.service.log.UserPageActionService;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.utils.OkHttpClientUtils;
import com.wakedata.dw.open.utils.rc4.SecureHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yiyufeng
 * @title UserPageActionController
 * @projectName bdp-open
 * @date 2019/8/16 10:22
 * @descprition
 */
@RestController
@Slf4j
@RequestMapping(value = "${spring.mvc.backend.prefix}/action")
@Api(value = "操作日志", tags = "操作日志")
public class UserPageActionController {

    @Autowired
    private UserPageActionService userPageActionService;

    @PostMapping("/report")
    @ApiOperation(value = "新增操作日志", tags = "新增操作日志", httpMethod = "POST")
    public ResultDTO actionReport(@Valid UserPageActionPo userPageAction,
                                  HttpServletRequest request) {
        userPageAction.setActionTime(new Date());
        userPageAction.setIp(request.getRemoteAddr());
        userPageAction.setRequestUrl(userPageAction.getRequestUrl());
        AuthUtils.setAppId(userPageAction);
        userPageActionService.insert(userPageAction);
        return new ResultDTO();
    }

    @PostMapping("/query/report")
    @ApiOperation(value = "操作日志查询", tags = "操作日志查询", httpMethod = "POST")
    @AuditLog
    public ResultDTO<Page<UserPageActionPo>> queryReport(@RequestBody UserActionPageQuery userActionPageQuery) {
        Page<UserPageActionPo> userPageActionPos = userPageActionService.queryReport(userActionPageQuery);
        PageResultDTO pageResultDTO = new PageResultDTO();
        if (CollectionUtils.isNotEmpty(userPageActionPos)) {
            pageResultDTO.setTotalCount((int) userPageActionPos.getTotal());
        } else {
            pageResultDTO.setTotalCount(0);
        }
        pageResultDTO.setData(userPageActionPos);
        pageResultDTO.setPageNo(userPageActionPos.getPageNum());
        pageResultDTO.setPageSize(userPageActionPos.getPageSize());
        return pageResultDTO;
    }

    @GetMapping("/query/audit/log/information")
    @ApiOperation(value = "单个操作日志详情查询", tags = "单个操作日志详情查询", httpMethod = "GET")
    public ResultDTO<UserPageActionPo> queryAuditLogInformation(@RequestParam Long id) {
        return ResultDTO.success(userPageActionService.queryAuditLogInformation(id));
    }

    @GetMapping("/query/identification/list")
    @ApiOperation(value = "查询ipaas所有用户账号的唯一标识",tags = "查询ipaas所有用户账号的唯一标识", httpMethod = "GET")
    public ResultDTO<List<String>> queryAllUserIdentification(@RequestParam(required = false) String userIdentification) {
        return ResultDTO.success(userPageActionService.queryAllUserIdentification(userIdentification));
    }

    @GetMapping("/query/menu")
    @ApiOperation("获取菜单列表")
    public ResultDTO<List<String>> queryMenu() {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setSuccess(true);
        resultDTO.setData(userPageActionService.queryMenu());
        return resultDTO;
    }

    public void group() throws IOException {
        String createDashboardUrl = "http://10.10.169.171:37799/webroot/decision/v5/api/conf/groups?bgy_username=penghe";
        Response response = OkHttpClientUtils.get(createDashboardUrl, null);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }

    public void packages(String packageId) throws IOException {
        String createDashboardUrl = "http://10.10.169.171:37799/webroot/decision/v5/api/conf/packs/" + packageId + "?bgy_username=bgy";
        Response response = OkHttpClientUtils.get(createDashboardUrl, null);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }


    /**
     * {
     * "from": [{
     * "reportId": "ffbc6371af34466698379aeb12d64e82"
     * }],
     * "to": {
     * "name": "地产公司1销售监控1sss",
     * "catalog": []
     * }
     * }
     *
     * @throws IOException
     */


    @GetMapping(value = "/listgroup")
    public void listgroup(
            String username,
            String packageName,
            String tableName
    ) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("bgy_username", SecureHelper.encryptAndSign(username, "123456789"));
        param.put("packageName", packageName);
        param.put("tableName", tableName);
        String createDashboardUrl = "https://10.10.169.171/webroot/decision/url/getPackages/public";
        Response response = OkHttpClientUtils.get(createDashboardUrl, param);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }

    @GetMapping(value = "/canclecol")
    public void canclecol(String tableName, String[] fieldName, String username) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("bgy_username", SecureHelper.encryptAndSign(username, "123456789"));
        param.put("tableName", tableName);
        StringBuilder stringBuilder = new StringBuilder();
        for (String field : fieldName) {
            stringBuilder.append(field).append(",");
        }
        param.put("fieldName", stringBuilder.substring(0, stringBuilder.length() - 1).toString());
        String createDashboardUrl = "https://10.10.169.171/webroot/decision/url/fieldPrivilege/deprive/public";
        Response response = OkHttpClientUtils.get(createDashboardUrl, param);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }

    @GetMapping(value = "/addcol")
    public void addcol(String tableName, String[] fieldName, String username) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("bgy_username", SecureHelper.encryptAndSign(username, "123456789"));
        param.put("tableName", tableName);
        StringBuilder stringBuilder = new StringBuilder();
        for (String field : fieldName) {
            stringBuilder.append(field).append(",");
        }
        param.put("fieldName", stringBuilder.substring(0, stringBuilder.length() - 1).toString());
        String createDashboardUrl = "https://10.10.169.171/webroot/decision/url/fieldPrivilege/grant/public";
        Response response = OkHttpClientUtils.get(createDashboardUrl, param);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }


    @GetMapping(value = "/mycol")
    public void mycol(String tableName, String username, String packageName) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("bgy_username", SecureHelper.encryptAndSign(username, "123456789"));
        param.put("tableName", tableName);
        param.put("packageName", packageName);
        String createDashboardUrl = "https://10.10.169.171/webroot/decision/url/getFields/public";
        Response response = OkHttpClientUtils.get(createDashboardUrl, param);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }

    @GetMapping(value = "/dashboard")
    public void dashboard(String username) throws IOException {
        Map<String, Object> param = new HashMap<>();
        param.put("bgy_username", SecureHelper.encryptAndSign(username, "123456789"));
        param.put("dir", "{\"catalog\":[]}");
        String createDashboardUrl = "https://10.10.169.171/webroot/decision/v5/api/platform/dashboard/list";
        Response response = OkHttpClientUtils.get(createDashboardUrl, param);
        String responseString = response.body().string();
        log.info("远端帆软返回 {}", responseString);
    }

    @Autowired
    private CacheService cacheService;

    @GetMapping(value = "/list")
    public void list() {
        log.info("list {}", cacheService.list());
    }

    @GetMapping(value = "/put")
    public void put() {
        log.info("put {}", cacheService.put());
    }

    @GetMapping(value = "/evit")
    public void evit() {
        log.info("evit {}", cacheService.evit());
    }

}
