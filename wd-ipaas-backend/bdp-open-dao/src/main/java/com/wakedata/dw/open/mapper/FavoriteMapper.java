package com.wakedata.dw.open.mapper;

import com.wakedata.dw.open.model.api.FavoritePo;
import com.wakedata.dw.open.model.domain.ApiCollectDo;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author tanzhi
 * @title FavoriteMapper
 * @date 2019/11/27 18:02
 * @projectName bdp-open
 * @descriptor
 */
public interface FavoriteMapper extends Mapper<FavoritePo> {

    /**
     * 我的收藏
     * @param username
     * @return
     */
    List<ApiCollectDo> myCollect(String username);

}
