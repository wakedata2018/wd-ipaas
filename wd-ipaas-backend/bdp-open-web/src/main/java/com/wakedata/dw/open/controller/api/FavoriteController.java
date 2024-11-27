package com.wakedata.dw.open.controller.api;

import com.github.pagehelper.Page;
import com.wakedata.dw.open.annotation.AuditLog;
import com.wakedata.dw.open.controller.BaseController;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.FavoritePo;
import com.wakedata.dw.open.model.query.PageQuery;
import com.wakedata.dw.open.service.api.DataAssetApiService;
import com.wakedata.dw.open.service.api.FavoriteService;
import com.wakedata.dw.open.util.WebUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author tanzhi
 * @title FavoriteController
 * @date 2019/11/27 18:05
 * @projectName bdp-open
 * @descriptor
 */
@RestController
@RequestMapping("${spring.mvc.backend.prefix}/business/collect")
@Api(value = "API收藏", tags = "API收藏")
@Validated
public class FavoriteController extends BaseController {


    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private DataAssetApiService dataAssetApiService;

    @PostMapping("/add")
    @ApiOperation("添加API收藏")
    @AuditLog
    public ResultDTO addToWhiteList(FavoritePo favoritePo) {
        favoritePo.setUsername(WebUtils.getCurrentUserInfo().getAccount());
        favoriteService.add(favoritePo);
        return new ResultDTO();
    }

    @PostMapping("/delete")
    @ApiOperation("取消API收藏")
    @AuditLog
    public ResultDTO deleteFromWhiteList(Integer collectId,
                                         @NotNull(message = "appId不能为空")Integer appId) {
        favoriteService.cancelCollect(collectId, appId, WebUtils.getCurrentUserInfo().getAccount());
        return new ResultDTO();
    }

    @GetMapping("/show")
    @ApiOperation("查询API收藏")
    @AuditLog
    public ResultDTO<FavoritePo> show(Integer favoriteId) {
        FavoritePo favoritePos = favoriteService.show(favoriteId);
        ResultDTO<FavoritePo> resultDTO = new ResultDTO<>();
        resultDTO.setData(favoritePos);
        return resultDTO;
    }

    @GetMapping("/list")
    @ApiOperation("查询API收藏列表")
    @AuditLog
    public ResultDTO<List<FavoritePo>> list(FavoritePo favoritePo) {
        favoritePo.setUsername(WebUtils.getCurrentUserInfo().getAccount());
        List<FavoritePo> favoritePos = favoriteService.find(favoritePo);
        ResultDTO<List<FavoritePo>> resultDTO = new ResultDTO<>();
        resultDTO.setData(favoritePos);
        return resultDTO;
    }

    @GetMapping("/my_collect")
    @ApiOperation("我的收藏")
    public PageResultDTO<Page<DataAssetApiPo>> myCollect(
            PageQuery pageQuery,
            Integer accessAppId,
            Integer dataAssetId,
            String keyword) {
        Page<DataAssetApiPo> apiCollectDos = dataAssetApiService.myCollect(
                WebUtils.getCurrentUserInfo().getAccount(),
                pageQuery,
                accessAppId,
                dataAssetId,
                keyword
        );
        PageResultDTO<Page<DataAssetApiPo>> resultDTO = new PageResultDTO<>();
        resultDTO.setTotalCount((int) apiCollectDos.getTotal());
        resultDTO.setData(apiCollectDos);
        return resultDTO;
    }
}
