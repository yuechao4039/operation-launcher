package com.hualala;

import com.dld.hll.financial.core.iface.vo.RequestHeader;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

/**
 * @author yuechao 2018/5/29
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/*.xml"})
public class FinancialTest  {


    protected RequestHeader header;

    protected Long userID = 1002L;

    protected String userName = "welcome";

    protected Long tenantID = 1002L;

    protected Long accountID = tenantID;

    @Before
    public void setUp() {

        header = new RequestHeader();

        header.setRequestURI("http://ilovehualala.io");
        header.setRemoteIp("10.10.22.11");
        header.setTrcid(UUID.randomUUID().toString());
        header.setDebug(true);

    }



}


