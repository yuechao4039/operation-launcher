package com.hualala;

import com.dld.hll.financial.base.api.login.SessionVoRequest;
import com.dld.hll.financial.base.api.login.SessionVoService;
import com.dld.hll.financial.core.enums.SourceTypeEnum;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionVoServiceTest extends FinancialTest {

    @Autowired
    private SessionVoService sessionVoService;


    @Test
    public void testKickOther() {

        SessionVoRequest sessionVoRequest = new SessionVoRequest();
        sessionVoRequest.setTenantLoginName("T6667");
        sessionVoRequest.setLoginName("6667");
        sessionVoRequest.setPasswd("654321");
        sessionVoRequest.setSource(SourceTypeEnum.HLL.toString());
        sessionVoRequest.setHeader(this.header);

        Long tenantID = 6667l;
        String JSESSIONID = "0EBF42F220729D2C68643ACDCC8A61C7";

        sessionVoService.kickOther(sessionVoRequest, tenantID, JSESSIONID);


    }
}
