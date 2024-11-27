package com.wakedata.dw.open.filter;

import com.wakedata.dw.open.config.DwOpenCommonConfig;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;

/**
 * @author zhengqinghui@wakedata.com
 * @date 2022/10/10 21:25
 */
@Component
public class WhitelistUrlCheck {

    private final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher("/");

    @Resource
    private DwOpenCommonConfig dwOpenCommonConfig;

    /**
     * 检查是否是license白名单URL(Web)
     *
     * @param requestPath 请求路径
     * @return 是否白名单结果
     */
    public boolean checkLicense(String requestPath){
        return dwOpenCommonConfig.getWebUnLicenseUri().stream().anyMatch(noSensitivePath ->
                ANT_PATH_MATCHER.match(noSensitivePath, requestPath));
    }
}
