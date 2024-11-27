package com.wakedata.dw.open.mapper.api;

import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.model.api.dto.AppAuthApiReqDTO;
import com.wakedata.dw.open.model.api.dto.AuthApiRespDTO;
import com.wakedata.dw.open.model.api.dto.NotAuthApiReqParam;
import com.wakedata.dw.open.model.domain.ApiCollectDo;
import com.wakedata.dw.open.model.domain.ApiCountResultDo;
import com.wakedata.dw.open.model.domain.AppTimesDo;
import com.wakedata.dw.open.model.query.ApiQuery;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author tanzhi
 * @title DataAssetApiMapper
 * @projectName bdp-open
 * @date 2019/9/5 11:55
 * @description
 */
public interface DataAssetApiMapper extends Mapper<DataAssetApiPo>, SelectByIdListMapper<DataAssetApiPo, Integer> {

    /**
     * 我收藏的API
     * `     * @param username
     *
     * @param dataAssetId
     * @param lesseeId
     * @return
     */
    List<ApiCollectDo> listByCollect(@Param("username") String username, @Param("dataAssetId") Integer dataAssetId,
                                     @Param("lesseeId") Long lesseeId, @Param("appId") Integer appId);


    /**
     * 已发布API数量
     *
     * @param lesseeId 租户id
     * @return 发布数
     */
    Integer publishedCount(@Param("lesseeId") Long lesseeId);

    /**
     * 我的收藏
     *
     * @param username
     * @param accessAppId
     * @param dataAssetId
     * @param lesseeId
     * @param keyword
     * @return
     */
    List<DataAssetApiPo> listMyCollect(
            @Param("username") String username,
            @Param("accessAppId") Integer accessAppId,
            @Param("dataAssetId") Integer dataAssetId,
            @Param("lesseeId") Long lesseeId,
            @Param("keyword") String keyword);


    /**
     * 统计历史调用次数
     *
     * @return Integer
     * @Param dataAssetId
     */
    Integer countHistoryAsset(@Param("dataAssetId") Integer dataAssetId);

    /**
     * 统计Api今天的调用次数
     *
     * @param dataAssetId
     * @param success
     * @param failed
     * @return Integer
     */
    Integer countTodayAsset(@Param("dataAssetId") Integer dataAssetId, @Param("success") Boolean success, @Param("failed") Boolean failed);

    /**
     * 查询
     *
     * @param subTime
     * @return
     */
    List<ApiCountResultDo> countTimesHour(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询近7天
     *
     * @param subTime
     * @return
     */
    List<ApiCountResultDo> countTimesDay(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询近24失败
     *
     * @param subTime
     * @return
     */
    List<ApiCountResultDo> countTimesHourError(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询近7天失败
     *
     * @param subTime
     * @return
     */
    List<ApiCountResultDo> countTimesDayError(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询24小时的qps
     *
     * @param subTime
     * @param dataAssetId
     * @return
     */
    List<ApiCountResultDo> countQpsHour(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询7天的qps
     *
     * @param subTime
     * @param dataAssetId
     * @return
     */
    List<ApiCountResultDo> countQpsDay(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询24小时的耗时
     *
     * @param subTime
     * @param dataAssetId
     * @return
     */
    List<ApiCountResultDo> countTakeTimeHour(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查询7天的耗时
     *
     * @param subTime
     * @param dataAssetId
     * @return
     */
    List<ApiCountResultDo> countTakeTimeDay(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查天调用排名
     *
     * @param subTime
     * @param dataAssetId
     */
    List<AppTimesDo> countAppTimesHour(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 查周调用排名
     *
     * @param subTime
     * @param dataAssetId
     * @return
     */
    List<AppTimesDo> countAppTimesDay(@Param("subTime") String subTime, @Param("dataAssetId") Integer dataAssetId);

    /**
     * 新增或更新API（需唯一索引支持）
     * <p>
     * 以"dataAssetApiMethod,reqMethod,apiGroupId"的唯一索引作为新增条件；并且更新时不改变已有的发布信息
     *
     * @param dataAssetApi API实体
     * @return 新增或更新结果
     */
    int addOrUpdateApi(@Param("dataAssetApi") DataAssetApiPo dataAssetApi);

    /**
     * 根据应用id、分组id和名称查询授权api
     *
     * @param dto 参数
     * @return 列表
     */
    List<AuthApiRespDTO> listByAppIdGroupIdName(AppAuthApiReqDTO dto);

    /**
     * 根据分组id列表查询API列表
     *
     * @param groupIds 分组id列表
     * @return 列表
     */
    List<DataAssetApiPo> listByGroupId(@Param("groupIds") List<Integer> groupIds);

    /**
     * 根据参数映射规则查询api列表
     *
     * @param respMappingRules 参数映射规则ids
     * @return 列表
     */
    List<DataAssetApiPo> listByRespMappingRule(@Param("respMappingRules") List<Integer> respMappingRules);


    /**
     * 查询应用还没有授权的api
     *
     * @param param 参数
     * @return api列表
     */
    List<DataAssetApiPo> listNotAuthApi(NotAuthApiReqParam param);

    /**
     * 根据id集合批量查询api数据
     *
     * @param ids id集合
     * @return api列表
     */
    List<DataAssetApiPo> findByIds(@Param("ids") List<Integer> ids);


    /**
     * 条件查询api管理列表
     *
     * @param query query
     * @return List<DataAssetApiPo>
     */
    List<DataAssetApiPo> selectPageList(@Param("query") ApiQuery query);

    /**
     * 批量更新API状态
     *
     * @param dataAssetApiIds apiId列表
     * @param publishStatus   发布状态
     * @param publisher       发布者
     * @return 列表
     */
    Boolean batchUpdateDataAssetApi(@Param("dataAssetApiIds") List<Integer> dataAssetApiIds, @Param("publishStatus") Integer publishStatus, @Param("publisher") String publisher);

    /**
     * 获取已发布的api列表
     * @param apiGroupId
     * @param lesseeId
     * @return
     */
    List<DataAssetApiPo> getPublishApiListByGroupId(@Param("apiGroupId") Integer apiGroupId,@Param("lesseeId") Long lesseeId);
}
