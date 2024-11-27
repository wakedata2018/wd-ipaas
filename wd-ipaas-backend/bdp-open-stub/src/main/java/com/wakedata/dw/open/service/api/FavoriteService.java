package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.model.api.FavoritePo;
import com.wakedata.dw.open.model.domain.ApiCollectDo;
import com.wakedata.dw.open.service.BaseDbService;

import java.util.List;

/**
 * @author tanzhi
 * @title FavoriteService
 * @date 2019/11/27 18:03
 * @projectName bdp-open
 * @descriptor
 */
public interface FavoriteService extends BaseDbService<FavoritePo> {

    /**
     * 我的收藏
     *
     * @param username
     * @return
     */
    List<ApiCollectDo> myCollect(String username);

    /**
     * 取消收藏
     *
     * @param collectId
     * @param username
     * @return
     */
    Integer cancelCollect(Integer collectId, Integer appId, String username);

    /**
     * @Description 根据条件删除数据
     * @Param
     * @Return
     * @Author wujiahao
     * @Date 2021/8/16
     **/
    void deleteByAppId(Integer dataAccessAppId);

}
