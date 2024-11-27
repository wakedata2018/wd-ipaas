package com.wakedata.dw.open.service.swagger;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.model.swagger.SwaggerInfoPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.swagger.dto.SwaggerInfoQueryDTO;

import java.util.List;

/**
 * Swagger信息服务
 *
 * @author chenshaopeng
 * date 2021/11/1
 */
public interface SwaggerInfoService extends BaseDbService<SwaggerInfoPo> {

    /**
     * 新增swagger信息
     *
     * @param swaggerInfo 新增对象
     */
    void addSwaggerInfo(SwaggerInfoPo swaggerInfo);

    /**
     * 新增swagger信息，通过文件上传的方式
     *
     * @param swaggerInfo 新增对象
     * @return Swagger信息
     */
    SwaggerInfoPo addSwaggerInfoFromFile(SwaggerInfoPo swaggerInfo);

    /**
     * 修改swagger信息
     *
     * @param swaggerInfo 修改对象
     */
    void updateSwaggerInfo(SwaggerInfoPo swaggerInfo);

    /**
     * 修改Swagger信息，通过上传swagger文件的方式
     *
     * @param swaggerInfo 修改对象
     * @return Swagger信息
     */
    SwaggerInfoPo updateSwaggerInfoFromFile(SwaggerInfoPo swaggerInfo);

    /**
     * 删除swagger信息
     *
     * @param swaggerInfoId 主键
     */
    void deleteSwaggerInfo(Integer swaggerInfoId);

    /**
     * 查询swagger详情
     *
     * @param swaggerInfoId 主键
     * @return 详情信息
     */
    SwaggerInfoPo detail(Integer swaggerInfoId);

    /**
     * 分页查询swagger信息
     *
     * @param queryDTO Swagger分页查询条件DTO
     * @return 分页查询结果
     */
    PageResultDTO<List<SwaggerInfoPo>> queryPage(SwaggerInfoQueryDTO queryDTO);

    /**
     * 更新swagger Api
     *
     * @param swaggerId  swagger信息Id
     * @param createUser 登录用户名
     * @throws Exception 异常信息
     */
    void updateSwaggerApi(Integer swaggerId, String createUser) throws Exception;
}