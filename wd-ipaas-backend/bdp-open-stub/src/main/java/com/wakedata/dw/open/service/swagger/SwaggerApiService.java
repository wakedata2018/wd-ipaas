package com.wakedata.dw.open.service.swagger;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.query.SwaggerApiQuery;
import com.wakedata.dw.open.model.swagger.SwaggerApiDO;
import com.wakedata.dw.open.service.api.dto.SwaggerApiDTO;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author WangChenSheng
 * date 2022/8/24 10:46
 */
public interface SwaggerApiService {

    /**
     * 条件查询swaggerApi列表
     *
     * @param swaggerApiQuery 查询条件
     * @return swaggerApi列表
     */
    PageResultDTO<List<SwaggerApiDTO>> pageList(SwaggerApiQuery swaggerApiQuery);

    /**
     * 新增临时swaggerApi
     *
     * @param swaggerApiDTO swaggerApi数据信息
     * @return SwaggerApiDTO
     * @throws Exception 异常
     */
    ResultDTO<Boolean> addSwaggerApi(SwaggerApiDTO swaggerApiDTO) throws Exception;

    /**
     * 根据id集合批量删除
     *
     * @param ids id集合
     * @return 删除结果
     */
    ResultDTO<Boolean> deleteBatch(List<Integer> ids);

    /**
     * 根据swaggerId获取swagger导入列表数据
     *
     * @param swaggerId swaggerId
     * @return 导入列表数据
     */
    List<SwaggerApiDTO> findBySwaggerId(Integer swaggerId);

    /**
     * 更新SwaggerApi导入状态
     *
     * @param swaggerApiDTO swaggerApiDTO
     * @return true/false
     */
    Boolean updateImportStatus(SwaggerApiDO swaggerApiDTO);

    /**
     * 根据SwaggerId删除临时API
     *
     * @param swaggerId swaggerId
     * @return true/false
     */
    void deleteBySwaggerId(Integer swaggerId);

    /**
     * 重新拉取Swagger
     *
     * @param swaggerApiDTO swaggerApiDTO(SwaggerId)
     * @return true/false
     * @throws Exception Exception
     */
    ResultDTO<Boolean> againImportTemporaryApi(SwaggerApiDTO swaggerApiDTO) throws Exception;

    /**
     * 普通导入临时API到主表
     *
     * @param swaggerApiDTO 导入参数
     * @return 导入结果
     */
    ResultDTO<String> addApiFromSwagger(SwaggerApiDTO swaggerApiDTO);

    /**
     * 覆盖导入临时API到主表
     *
     * @param swaggerApiDTO 临时API主键id集合 和 swaggerId
     * @return 导入结果
     */
    ResultDTO<String> convertDateAssetApiFromTemporary(SwaggerApiDTO swaggerApiDTO);

    /**
     * 根据example查询数据
     *
     * @param example Example
     * @return swagger临时表集合
     */
    List<SwaggerApiDTO> selectByExample(Example example);

}
