package com.hualala;

import com.dld.hll.financial.interfaces.apm.grpc.RegMasterServiceGrpc;
import com.dld.hll.financial.interfaces.apm.grpc.RegQueryReq;
import com.dld.hll.financial.interfaces.apm.grpc.RegQueryReqOrBuilder;
import com.dld.hll.financial.interfaces.apm.grpc.RequestHeader;
import com.google.gson.Gson;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class GrpClientTest {

    Properties properties = new Properties();
    @Before
    public void setUp() {
        properties.put("host", "172.16.32.207");
        properties.put("port", "18081");
        properties.put("usePlaintext", "true");
    }


    @Test
    public void testGrpc() {
        ManagedChannelBuilder<?> builder =
                ManagedChannelBuilder.forAddress(properties.getProperty("host"), Integer.parseInt(properties.getProperty("port")));

        if (Boolean.valueOf(properties.getProperty("usePlaintext"))) {
            builder.usePlaintext(true);
        }
//        if (properties.getIdleTimeout() != null) {
//            builder.idleTimeout(properties.getIdleTimeout(), TimeUnit.SECONDS);
//        }
//        if (properties.getKeepAliveTime() != null) {
//            builder.keepAliveTime(properties.getKeepAliveTime(), TimeUnit.SECONDS);
//        }
//        if (properties.getKeepAliveTimeout() != null) {
//            builder.keepAliveTimeout(properties.getKeepAliveTimeout(), TimeUnit.SECONDS);
//        }
        ManagedChannel managedChannel = builder.build();

        RequestHeader requestHeader =  RequestHeader.newBuilder().setTrcid("asodiufaosiduf").build();

        RegMasterServiceGrpc.RegMasterServiceBlockingStub stub = RegMasterServiceGrpc.newBlockingStub(managedChannel);
        RegQueryReq req =  RegQueryReq.newBuilder()
                .setAccountID(6667L).setTenantID(6667L).setUserID(1111L).setUserName("yuechao")
                .setBeanName("regMasterServiceImpl")
                .setHeader(requestHeader).build();
        com.dld.hll.financial.interfaces.apm.grpc.RegQueryResp resp = stub.query(req);
        System.out.println(new Gson().toJson(resp));
    }
}
