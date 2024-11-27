package com.wakedata.dw.open.service.lessee;

import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.model.lessee.OpenAccountPo;
import com.wakedata.dw.open.service.BaseDbService;
import com.wakedata.dw.open.service.lessee.dto.AccountLoginDTO;
import com.wakedata.dw.open.service.lessee.dto.IpaasUserInfoDTO;
import com.wakedata.dw.open.userinfo.IpaasUserInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 账号登录相关
 * @author luomeng
 * @date 2022/8/4 14:23
 */
public interface OpenAccountLoginService extends BaseDbService<OpenAccountPo> {


    /**
     * 账号登录
     * @param accountLoginDTO
     * @return
     */
    ResultDTO<IpaasUserInfoDTO> login(HttpServletRequest request, HttpServletResponse response,AccountLoginDTO accountLoginDTO);


    /**
     * 退出
     * @return
     */
    ResultDTO<Boolean> logout(HttpServletRequest request, HttpServletResponse response);


    /**
     * 查询用户信息
     * @return
     */
    ResultDTO<IpaasUserInfoDTO> getAccountInfo(HttpServletRequest request);
}
