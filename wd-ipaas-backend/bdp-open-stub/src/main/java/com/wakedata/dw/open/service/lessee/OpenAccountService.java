package com.wakedata.dw.open.service.lessee;

import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.lessee.OpenAccountPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountDTO;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountQuery;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountSimpleDTO;

import java.util.List;

/**
 * ipaas账号管理
 * @author luomeng
 * @date 2022/8/2 17:34
 */
public interface OpenAccountService extends BaseDbService<OpenAccountPo> {

    /**
     * 创建账号
     * @param openAccountDTO
     * @return
     */
    ResultDTO<Boolean> createAccount(OpenAccountDTO openAccountDTO);


    /**
     * 获取账号信息
     * @param id
     * @return
     */
    ResultDTO<OpenAccountDTO> getAccountInfo(Long id);

    /**
     * 更新账号信息
     * @param openAccountDTO
     * @return
     */
    ResultDTO<Boolean> updateAccount(OpenAccountDTO openAccountDTO);

    /**
     * 删除账号
     * @param id
     * @return
     */
    ResultDTO<Boolean> deleteAccount(Long id);

    /**
     * 重置密码
     * @param openAccountSimpleDTO
     * @return
     */
    ResultDTO<Boolean> resetPassword(OpenAccountSimpleDTO openAccountSimpleDTO);

    /**
     * 查询列表
     * @param openAccountQuery
     * @return
     */
    PageResultDTO<List<OpenAccountDTO>> list(OpenAccountQuery openAccountQuery);
}
