package com.hualala;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lipan on 2017/8/13.
 * Describe: Http协议模拟
 */
public class SocketTest {



    public static void main(String[] args) throws Exception {
        LoginTest login = new LoginTest();
        EmpTest emp = new EmpTest();
        SummaryTest summaryTest = new SummaryTest();
        for (int i = 0; i < 100000; i++) {
//            login.login();
            System.out.println("============login");
            summaryTest.summaryquery();
            System.out.println("============summaryquery");

//            emp.empquery();
            System.out.println("============empquery");
        }
    //
    }










}

