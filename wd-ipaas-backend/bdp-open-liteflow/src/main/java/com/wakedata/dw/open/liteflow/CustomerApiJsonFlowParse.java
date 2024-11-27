package com.wakedata.dw.open.liteflow;

import com.wakedata.common.core.GlobalApplicationContext;
import com.wakedata.dw.open.service.impl.api.attr.ApiLiteFlowChainHelper;
import com.yomahub.liteflow.parser.el.ClassJsonFlowELParser;
import lombok.extern.slf4j.Slf4j;


/**
 * 自定义api流程解析器,替换掉使用文件存储流程链
 *
 * @author luomeng
 * @date 2022/10/13 14:30
 */
@Slf4j
public class CustomerApiJsonFlowParse extends ClassJsonFlowELParser {

    @Override
    public String parseCustom() {
        Long dataAssetApiId = LiteFlowApiChainThreadLocal.getApiChainKey();
        String liteFlowChain = GlobalApplicationContext.getBean(ApiLiteFlowChainHelper.class).getLiteFlowChain(dataAssetApiId);
        LiteFlowApiChainThreadLocal.removeApiChainKey();
        return liteFlowChain;
    }
}
