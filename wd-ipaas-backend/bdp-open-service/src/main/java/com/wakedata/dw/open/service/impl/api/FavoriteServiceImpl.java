package com.wakedata.dw.open.service.impl.api;

import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.FavoriteMapper;
import com.wakedata.dw.open.model.api.FavoritePo;
import com.wakedata.dw.open.model.domain.ApiCollectDo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.api.FavoriteService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author tanzhi
 * @title FavoriteServiceImpl
 * @date 2019/11/27 18:04
 * @projectName bdp-open
 * @descriptor
 */
@Service
public class FavoriteServiceImpl extends BaseServiceImpl<FavoritePo, FavoriteMapper> implements FavoriteService {
    @Resource
    private CurdService<FavoritePo> crudserService;

    @Autowired
    @Override
    protected void init(CurdService<FavoritePo> curdService, FavoriteMapper mapper) {
        super.set(curdService, mapper);
    }

    @Override
    public List<ApiCollectDo> myCollect(String username) {
        List<ApiCollectDo> apiCollectDos = getMapper().myCollect(username);
        return apiCollectDos;
    }

    @Override
    public Integer cancelCollect(Integer dataAssetApiId, Integer appId, String username) {
        FavoritePo favoritePo = new FavoritePo();
        favoritePo.setUsername(username);
        favoritePo.setDataAssetApiId(dataAssetApiId);
        favoritePo.setDataAccessAppId(appId);
        int delete = getCurdService().delete(favoritePo, getMapper());
        if (delete != 1) {
            throw new OpenException(MsgCodeEnum.w_cancel_collect_fail);
        }
        return delete;
    }

    @Override
    public void deleteByAppId(Integer dataAccessAppId) {
        FavoritePo favoritePo = new FavoritePo();
        favoritePo.setDataAccessAppId(dataAccessAppId);
        crudserService.delete(favoritePo, getMapper());
    }
}
