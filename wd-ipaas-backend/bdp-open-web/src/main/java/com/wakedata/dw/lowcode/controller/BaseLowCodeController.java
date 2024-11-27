package com.wakedata.dw.lowcode.controller;

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.wakedata.dw.lowcode.dto.BaseDTO;
import com.wakedata.dw.lowcode.model.AppPo;
import com.wakedata.dw.lowcode.util.AppIdContext;
import com.wakedata.dw.open.service.utils.AuthUtils;
import com.wakedata.dw.open.util.WebUtils;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;

/**
 * @author wanghu@wakedata.com
 * @title 低代码控制器基类
 * @date 2021/11/24
 * @since v1.0.0
 */
public class BaseLowCodeController {

    protected <DTO extends BaseDTO, PO extends AppPo> PO convertDtoToPo(DTO dto, Class<PO> poClass) {
        PO po = BeanUtils.instantiateClass(poClass);
        BeanUtil.copyProperties(dto, po);

        setEditUserAndEpId(po);
        return po;
    }

    private void setEditUserAndEpId(AppPo appPo) {
        String loginName = WebUtils.getCurrentUserInfo().getAccount();
        if (Objects.isNull(appPo.getId())) {
            appPo.setCreateBy(loginName);
        }
        appPo.setUpdateBy(loginName);
        appPo.setAppId(AppIdContext.getAppId());
        appPo.setEpId(AuthUtils.currentAppId().intValue());
    }

    protected <DTO extends BaseDTO> DTO convertPoToDto(AppPo po, Class<DTO> tClass) {
        return Optional.ofNullable(po).map(poItem -> convertSinglePoToDto(tClass, poItem)).orElse(null);
    }

    private <DTO extends BaseDTO> DTO convertSinglePoToDto(Class<DTO> tClass, AppPo poItem) {
        DTO dto = BeanUtils.instantiateClass(tClass);
        BeanUtil.copyProperties(poItem, dto);

        dto.setOperator(poItem.getUpdateBy());
        return dto;
    }

    protected <DTO extends BaseDTO, PO extends AppPo> List<DTO> convertPoListToDto(List<PO> pos, Class<DTO> tClass) {
        return Optional.ofNullable(pos)
            .map(poList -> {
                return poList.stream().map(po -> convertSinglePoToDto(tClass, po)).collect(Collectors.toList());
            })
            .orElse(Collections.emptyList());
    }

    protected <PO extends AppPo, DTO extends BaseDTO> Page<?> covertPoListToDto(
        Page<PO> poPage, Class<DTO> dtoClass) {

        if (CollectionUtils.isEmpty(poPage.getResult())) {
            return poPage;
        }

        Page<DTO> pageResult = new Page<>();
        BeanUtil.copyProperties(poPage, pageResult);

        List<DTO> dtoList = poPage.getResult().stream()
            .map(po -> convertSinglePoToDto(dtoClass, po)).collect(Collectors.toList());

        pageResult.addAll(dtoList);
        return pageResult;
    }


}
