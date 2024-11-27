package com.wakedata.dw.open.service.impl.lessee;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.wakedata.common.core.util.BeanUtil;
import com.wakedata.dw.open.constant.DwOpenConstant;
import com.wakedata.dw.open.dto.PageResultDTO;
import com.wakedata.dw.open.dto.ResultDTO;
import com.wakedata.dw.open.enums.MsgCodeEnum;
import com.wakedata.dw.open.enums.RoleAuthEnum;
import com.wakedata.dw.open.enums.StatusEnum;
import com.wakedata.dw.open.exception.OpenException;
import com.wakedata.dw.open.mapper.lessee.OpenAccountMapper;
import com.wakedata.dw.open.mapper.lessee.OpenLesseeMapper;
import com.wakedata.dw.open.model.lessee.OpenAccountPo;
import com.wakedata.dw.open.model.lessee.OpenLesseePo;
import com.wakedata.dw.open.model.query.AccountQuery;
import com.wakedata.dw.open.model.vo.OpenAccountVo;
import com.wakedata.dw.open.service.CurdService;
import com.wakedata.dw.open.service.impl.BaseServiceImpl;
import com.wakedata.dw.open.service.lessee.OpenAccountService;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountDTO;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountQuery;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountSimpleDTO;
import com.wakedata.wd.permission.enums.IpaasRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author luomeng
 * @date 2022/8/3 15:08
 */
@Service
@Slf4j
public class OpenAccountServiceImpl extends BaseServiceImpl<OpenAccountPo, OpenAccountMapper> implements OpenAccountService {


    @Resource
    private OpenLesseeMapper openLesseeMapper;

    @Autowired
    @Override
    protected void init(CurdService<OpenAccountPo> curdService, OpenAccountMapper mapper) {
        super.set(curdService,mapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> createAccount(OpenAccountDTO openAccountDTO) {

        int count = this.getMapper().selectAccountNum(openAccountDTO.getUserIdentification(),openAccountDTO.getId());
        if(count > 0){
            throw new OpenException(MsgCodeEnum.w_user_account_exist);
        }
        //校验是否已经添加过平台管理员，平台管理员只能有一个
        if(IpaasRoleEnum.SUPER_ADMIN_ROLE.getId().equals(openAccountDTO.getRelateRoleId())){
            AccountQuery account = new AccountQuery();
            account.setRelateRoleId(openAccountDTO.getRelateRoleId());
            Integer adminCount = this.getMapper().selectListCount(account);
            if(ObjectUtil.isNotNull(adminCount) && adminCount > 0){
                throw new OpenException(MsgCodeEnum.w_user_account_admin);
            }
        }
        //添加租户
        OpenLesseePo openLesseePo = addOpenLesseePo(openAccountDTO);
        //添加账号
        OpenAccountPo openAccountPo = BeanUtil.copy(openAccountDTO, OpenAccountPo.class);
        openAccountPo.setLesseeId(openLesseePo.getId());
        openAccountPo.setIsAdmin(ObjectUtil.isNull(openAccountDTO.getIsAdmin())? RoleAuthEnum.DEVELOPER.getCode() :openAccountDTO.getIsAdmin());
        openAccountPo.setStatus(StatusEnum.ACTIVE.getCode());
        openAccountPo.setPassword(DigestUtil.md5Hex(String.join(DwOpenConstant.JOIN_POINT,openAccountDTO.getPassword(),DwOpenConstant.USER_PASSWORD_SALT)));

        return ResultDTO.success(super.add(openAccountPo) > 0);
    }


    @Override
    public ResultDTO<OpenAccountDTO> getAccountInfo(Long id) {
        OpenAccountVo openAccountVo = this.getMapper().getAccountInfo(id);
        return ResultDTO.success(BeanUtil.copy(openAccountVo, OpenAccountDTO.class));
    }

    @Override
    public ResultDTO<Boolean> updateAccount(OpenAccountDTO openAccountDTO) {

        int count = this.getMapper().selectAccountNum(openAccountDTO.getUserIdentification(),openAccountDTO.getId());
        if(count > 0){
            throw new OpenException(MsgCodeEnum.w_user_account_exist);
        }
        return ResultDTO.success(super.update(getUpdateAccountPo(openAccountDTO)) > 0);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultDTO<Boolean> deleteAccount(Long id) {
        OpenAccountVo accountInfo = this.getMapper().getAccountInfo(id);
        if(accountInfo == null){
            throw new OpenException(MsgCodeEnum.w_user_not_found);
        }
        //平台管理员不能被删除
        if(IpaasRoleEnum.SUPER_ADMIN_ROLE.getId().equals(accountInfo.getRelateRoleId())){
            throw new OpenException(MsgCodeEnum.w_user_not_delete);
        }
        //租户假删除
        OpenLesseePo openLesseePo = new OpenLesseePo();
        openLesseePo.setId(accountInfo.getLesseeId());
        openLesseePo.setIsDeleted(1);
        openLesseeMapper.updateByPrimaryKey(openLesseePo);
        return ResultDTO.success(this.getMapper().deleteByPrimaryKey(id) > 0);
    }

    @Override
    public ResultDTO<Boolean> resetPassword(OpenAccountSimpleDTO openAccountSimpleDTO) {

        OpenAccountPo openAccountPo = new OpenAccountPo();
        openAccountPo.setId(openAccountSimpleDTO.getId());
        openAccountPo.setPassword(DigestUtil.md5Hex(String.join(DwOpenConstant.JOIN_POINT,openAccountSimpleDTO.getNewPassword(),DwOpenConstant.USER_PASSWORD_SALT)));
        return ResultDTO.success(super.update(openAccountPo) > 0);
    }

    @Override
    public PageResultDTO<List<OpenAccountDTO>> list(OpenAccountQuery openAccountQuery) {
        AccountQuery query = BeanUtil.copy(openAccountQuery, AccountQuery.class);
        Integer total = this.getMapper().selectListCount(query);
        PageResultDTO<List<OpenAccountDTO>> pageResultDTO = new PageResultDTO<>();
        pageResultDTO.setTotalCount(total);
        if(total > 0){
            pageResultDTO.setData(BeanUtil.copyList(this.getMapper().selectList(query),OpenAccountDTO.class));
        }
        return pageResultDTO;
    }

    private OpenAccountPo getUpdateAccountPo(OpenAccountDTO openAccountDTO) {
        OpenAccountPo openAccountPo = new OpenAccountPo();
        openAccountPo.setId(openAccountDTO.getId());
        openAccountPo.setUserIdentification(openAccountDTO.getUserIdentification());
        openAccountPo.setName(openAccountDTO.getName());
        openAccountPo.setPhone(openAccountDTO.getPhone());
        openAccountPo.setIsAdmin(ObjectUtil.isNull(openAccountDTO.getIsAdmin())? RoleAuthEnum.DEVELOPER.getCode() :openAccountDTO.getIsAdmin());
        return openAccountPo;
    }

    private OpenLesseePo addOpenLesseePo(OpenAccountDTO openAccountDTO) {
        OpenLesseePo openLesseePo = new OpenLesseePo();
        openLesseePo.setTenantId(openAccountDTO.getTenantId());
        if(StrUtil.isNotBlank(openAccountDTO.getTenantName())){
            openLesseePo.setName(openAccountDTO.getTenantName());
        }else{
            openLesseePo.setName(openAccountDTO.getName());
        }

        openLesseePo.setCreateTime(new Date());
        openLesseePo.setUpdateTime(new Date());
        openLesseePo.setIsDeleted(0);
        openLesseeMapper.insert(openLesseePo);
        return openLesseePo;
    }

    public static void main(String[] args) {
        System.out.println(DigestUtil.md5Hex(String.join(DwOpenConstant.JOIN_POINT,"TE&YHFWAT134",DwOpenConstant.USER_PASSWORD_SALT)));
    }
}
