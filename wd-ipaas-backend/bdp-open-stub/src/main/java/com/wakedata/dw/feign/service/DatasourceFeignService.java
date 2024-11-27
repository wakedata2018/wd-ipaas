package com.wakedata.dw.feign.service;

import com.wakedata.common.core.dto.ResultDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 提供给datasource使用的feign接口
 * @author luomeng
 * @date 2023/2/20 18:35
 */
@FeignClient(value = "wd-ipaas",path = "/dw/open/rpc/datasource")
public interface DatasourceFeignService {

    /**
     * 用户认证
     * @param sessionId
     * @return
     */
    @GetMapping(value = "/userAuth")
    ResultDTO<IpaasUserInfo> userAuth(@RequestParam("sessionId") String sessionId);




}
