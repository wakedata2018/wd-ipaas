package com.wakedata.dw.open.service.api;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.function.custom.CustomFunction;
import com.wakedata.dw.open.service.api.dto.CustomFunctionDTO;
import com.wakedata.dw.open.service.api.dto.CustomFunctionQuery;

import java.util.List;

/**
 * 自定义函数
 * @author luomeng
 * @date 2022/11/3 14:16
 */
public interface CustomFunctionService {


    /**
     * 获取所有的自定义函数
     * @param customFunctionQuery
     * @return
     */
    PageResultDTO<List<CustomFunctionDTO>> getAllCustomFunctionList(CustomFunctionQuery customFunctionQuery);

    /**
     * 添加自定义函数
     * @param customFunctionDTO
     * @return
     */
    ResultDTO<Boolean> addCustomFunction(CustomFunctionDTO customFunctionDTO);

    /**
     * 更新自定义函数
     * @param customFunctionDTO
     * @return
     */
    ResultDTO<Boolean> updateCustomFunction(CustomFunctionDTO customFunctionDTO);

    /**
     * 调试自定义函数
     * @param customFunction
     * @return
     */
    Object debugCustomFunction(CustomFunction customFunction);

    /**
     * 更新自定义函数状态
     * @param lesseeId
     * @param customerFunctionId
     * @param status
     * @return
     */
    Boolean updateCustomFunctionStatus(Long lesseeId, Long customerFunctionId, Integer status);

    /**
     * 根据id查询自定义函数
     * @param customerFunctionId
     * @return
     */
    ResultDTO<CustomFunctionDTO> getCustomFunctionById(Long customerFunctionId);

}
