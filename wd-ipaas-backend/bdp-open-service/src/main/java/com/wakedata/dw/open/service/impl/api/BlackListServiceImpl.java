package com.wakedata.dw.open.service.impl.api;

import com.wakedata.dw.open.mapper.api.BlackListMapper;
import com.wakedata.dw.open.model.api.BlackListPo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.BlackListService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author liuzheng
 * @title BlackListServiceImpl
 * @date 2021/4/6 19:46
 * @projectName bdp-open
 * @description
 */
@Service
public class BlackListServiceImpl extends BaseServiceImpl<BlackListPo, BlackListMapper> implements BlackListService {

    private static final String REGEX_SPLIT = ",";

    @Autowired
    @Override
    protected void init(CurdService<BlackListPo> curdService, BlackListMapper mapper) {
        super.set(curdService, mapper);
    }

    /**
     * 查询列表.
     * @param appId 接入应用id.
     * @return 对应接入应用黑名单列表.
     */
    @Override
    public List<BlackListPo> showBlackList(Integer appId) {
        return getMapper().getBlackList(appId);
    }

    /**
     * 添加、修改访问黑名单.
     * @param appId 接入应用id.
     * @param ips 黑名单字符串.
     * @return 是否成功.
     */
    @Override
    public Boolean addToBlackList(Integer appId, String ips) {
        BlackListPo po = new BlackListPo();
        po.setDataAccessAppId(appId);
        getMapper().delete(po);
        if (StringUtils.isNotEmpty(ips)) {
            String[] ipArray = ips.split(REGEX_SPLIT);
            ArrayList<BlackListPo> blackListPos = new ArrayList<>(ipArray.length);
            for (String ip : ipArray) {
                BlackListPo blackListPo = new BlackListPo();
                blackListPo.setIp(ip);
                blackListPo.setDataAccessAppId(appId);
                Date date = new Date();
                blackListPo.setCreateTime(date);
                blackListPo.setUpdateTime(date);
                blackListPos.add(blackListPo);
            }
            getMapper().insertList(blackListPos);
        }
        return true;
    }

    /**
     * 校验是否在黑名单中.
     * @param appKey 接入应用appKey.
     * @return 返回对应黑名单列表.
     */
    @Override
    public Set<String> checkBlockList(String appKey) {
        return getMapper().checkBlockList(appKey);
    }
}
