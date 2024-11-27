package com.wakedata.dw.open.service.impl.api;

import cn.hutool.core.util.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.function.custom.CustomFunction;
import com.wakedata.dw.open.function.custom.CustomFunctionStatus;
import com.wakedata.dw.open.function.custom.GroovyScriptEngine;
import com.wakedata.dw.open.mapper.api.CustomFunctionMapper;
import com.wakedata.dw.open.mapper.api.CustomFunctionRelationApiMapper;
import com.wakedata.dw.open.model.api.CustomFunctionPo;
import com.wakedata.dw.open.model.api.CustomFunctionRelationApiPo;
import com.wakedata.dw.open.model.api.DataAssetApiPo;
import com.wakedata.dw.open.service.api.CustomFunctionService;
import com.wakedata.dw.open.service.api.dto.CustomFunctionDTO;
import com.wakedata.dw.open.service.api.dto.CustomFunctionQuery;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 自定义函数接口实现
 * @author luomeng
 * @date 2022/11/3 14:35
 */
@Service
public class CustomFunctionServiceImpl implements CustomFunctionService {

    @Resource
    private CustomFunctionMapper customFunctionMapper;

    @Resource
    private CustomFunctionRelationApiMapper customFunctionRelationApiMapper;

    @Override
    public PageResultDTO<List<CustomFunctionDTO>> getAllCustomFunctionList(CustomFunctionQuery customFunctionQuery) {
        PageHelper.startPage(customFunctionQuery.getPageNo(),customFunctionQuery.getPageSize());
        CustomFunctionPo functionPo = new CustomFunctionPo();
        functionPo.setLesseeId(customFunctionQuery.getLesseeId());
        PageInfo<CustomFunctionPo> pageInfo = new PageInfo<>(customFunctionMapper.select(functionPo));
        PageResultDTO<List<CustomFunctionDTO>> pageResultDTO = new PageResultDTO<>(customFunctionQuery.getPageNo(),customFunctionQuery.getPageSize());
        pageResultDTO.setTotalCount(Long.valueOf(pageInfo.getTotal()).intValue());
        pageResultDTO.setData(BeanUtil.copyList(pageInfo.getList(), CustomFunctionDTO.class));
        return pageResultDTO;
    }

    /**
     * 校验函数名称是否重复
     * @param lesseeId
     * @param id
     * @param funcName
     * @return
     */
    private Boolean checkCustomFuncNameIsRepeat(Long lesseeId,Long id,String funcName){
        Example example = new Example(CustomFunctionPo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("lesseeId",lesseeId).andEqualTo("funcName",funcName);
        if(ObjectUtil.isNotNull(id) && id > 0){
            criteria.andNotEqualTo("id",id);
        }
        int count = customFunctionMapper.selectCountByExample(example);
        return count > 0;
    }

    @Override
    public ResultDTO<Boolean> addCustomFunction(CustomFunctionDTO customFunctionDTO) {
        //校验函数名是否重复
        if(checkCustomFuncNameIsRepeat(customFunctionDTO.getLesseeId(),null,customFunctionDTO.getFuncName())){
            throw new OpenException("自定义函数名称重复");
        }
        CustomFunctionPo functionPo = BeanUtil.copy(customFunctionDTO, CustomFunctionPo.class);
        functionPo.setStatus(CustomFunctionStatus.StatusEnum.OUTLINE.getCode());
        functionPo.setDebugStatus(CustomFunctionStatus.DebugStatusEnum.NOT_TEST.getCode());
        functionPo.setCreateTime(new Date());
        functionPo.setUpdateTime(new Date());
        int insert = customFunctionMapper.insert(functionPo);
        return ResultDTO.success(insert > 0);
    }

    @Override
    public ResultDTO<Boolean> updateCustomFunction(CustomFunctionDTO customFunctionDTO) {
        CustomFunctionPo customFunctionPo = this.getCustomFunctionPo(customFunctionDTO.getId());
        //已上线不允许修改
        if(customFunctionPo == null){
            throw new OpenException("自定义函数不存在");
        }
        if(CustomFunctionStatus.StatusEnum.ONLINE.getCode().equals(customFunctionPo.getStatus())){
            throw new OpenException("已上线的自定义函数不允许修改");
        }
        //校验函数名是否重复
        if(checkCustomFuncNameIsRepeat(customFunctionDTO.getLesseeId(),customFunctionDTO.getId(),customFunctionDTO.getFuncName())){
            throw new OpenException("自定义函数名称重复");
        }
        CustomFunctionPo functionPo = BeanUtil.copy(customFunctionDTO, CustomFunctionPo.class);
        functionPo.setUpdateTime(new Date());
        int update = customFunctionMapper.updateByPrimaryKeySelective(functionPo);
        return ResultDTO.success(update > 0);
    }

    @Override
    public Object debugCustomFunction(CustomFunction customFunction) {
        CustomFunctionPo customFunctionPo = new CustomFunctionPo();
        customFunctionPo.setLesseeId(customFunction.getLesseeId());
        customFunctionPo.setFuncName(customFunction.getName());
        customFunctionPo = customFunctionMapper.selectOne(customFunctionPo);
        if(customFunctionPo == null){
            throw new OpenException("自定义函数不存在");
        }
        customFunction.setReturnType(customFunctionPo.getFuncReturn());
        customFunction.setCode(customFunctionPo.getFuncCode());
        customFunction.setDesc(customFunctionPo.getFuncDesc());
        customFunction.setLanguage(customFunctionPo.getFuncLanguage());
        try {
            Object execute = GroovyScriptEngine.execute(customFunction);
            CustomFunctionPo function = new CustomFunctionPo();
            function.setId(customFunctionPo.getId());
            function.setDebugStatus(CustomFunctionStatus.DebugStatusEnum.PASS_TEST.getCode());
            function.setUpdateTime(new Date());
            customFunctionMapper.updateByPrimaryKeySelective(function);
            return execute;
        }catch (OpenException e){
            CustomFunctionPo function = new CustomFunctionPo();
            function.setId(customFunctionPo.getId());
            function.setDebugStatus(CustomFunctionStatus.DebugStatusEnum.FAIL_TEST.getCode());
            function.setUpdateTime(new Date());
            customFunctionMapper.updateByPrimaryKeySelective(function);
            throw e;
        }
    }

    @Override
    public Boolean updateCustomFunctionStatus(Long lesseeId, Long customerFunctionId, Integer status) {
        CustomFunctionPo customFunctionPo = this.getCustomFunctionPo(customerFunctionId);
        if(customFunctionPo == null){
            throw new OpenException("自定义函数不存在");
        }
        //已被调用不允许下线
        if(CustomFunctionStatus.StatusEnum.OFFLINE.getCode().equals(status)) {
            CustomFunctionRelationApiPo relationApiPo = new CustomFunctionRelationApiPo();
            relationApiPo.setLesseeId(lesseeId);
            relationApiPo.setCustomFuncName(customFunctionPo.getFuncName());
            int count = customFunctionRelationApiMapper.selectCount(relationApiPo);
            if(count > 0){
                throw new OpenException("自定义函数已被api调用，不允许下线");
            }
        }
        //未测试通过不允许上线
        if(!CustomFunctionStatus.DebugStatusEnum.PASS_TEST.getCode().equals(customFunctionPo.getDebugStatus())
            && CustomFunctionStatus.StatusEnum.ONLINE.getCode().equals(status)){
            throw new OpenException("未测试通过不允许上线");
        }
        CustomFunctionPo functionPo = new CustomFunctionPo();
        functionPo.setId(customerFunctionId);
        functionPo.setStatus(status);
        functionPo.setUpdateTime(new Date());
        return customFunctionMapper.updateByPrimaryKeySelective(functionPo) > 0;
    }


    @Override
    public ResultDTO<CustomFunctionDTO> getCustomFunctionById(Long customerFunctionId) {
        if(ObjectUtil.isNull(customerFunctionId)){
            throw new OpenException("自定义函数id不能为空");
        }
        CustomFunctionPo functionPo = getCustomFunctionPo(customerFunctionId);
        return ResultDTO.success(BeanUtil.copy(functionPo, CustomFunctionDTO.class));
    }

    private CustomFunctionPo getCustomFunctionPo(Long customerFunctionId) {
        CustomFunctionPo customFunctionPo = new CustomFunctionPo();
        customFunctionPo.setId(customerFunctionId);
        return customFunctionMapper.selectByPrimaryKey(customFunctionPo);
    }
}
