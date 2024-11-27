package com.wakedata.dw.open.service;

import com.wakedata.dw.DwOpenWebApplication;
import com.wakedata.dw.DwOpenWebApplicationTest;
import com.wakedata.dw.open.service.lessee.OpenAccountService;
import com.wakedata.dw.open.service.lessee.dto.OpenAccountDTO;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author luomeng
 * @date 2022/8/3 16:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DwOpenWebApplication.class)
public class OpenAccountTest {

    @Resource
    private OpenAccountService openAccountService;

    @Test
    public void createAccount(){
        OpenAccountDTO openAccountDTO = new OpenAccountDTO();
        openAccountDTO.setUserIdentification("test01");
        openAccountDTO.setName("test");
        openAccountDTO.setPassword("abc123");
        openAccountDTO.setRelateRoleId("123");
        openAccountDTO.setTenantId(1L);
        openAccountDTO.setTenantName("test");
        openAccountDTO.setPhone("13011111111");
        openAccountService.createAccount(openAccountDTO);
    }
}
