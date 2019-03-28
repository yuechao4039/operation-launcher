package com.hualala;

import org.junit.Test;

/**
 * @author yuechao 2018/11/22
 */
public class RegTest {

    @Test
    public void test() {


        String uri = "/login.html";
        String pattern = ".*/login.html$";
        uri = "/static/css/login.38584f82183b51992e496ce56464417e.css";
        pattern = ".*static.*";
        System.out.println(uri.matches(pattern));
    }

    @Test
    public void pNmae() {
        String packageName ="com.dld.hll.financial.base.api.ivcode";
        String p = "com.dld.hll.financial.";
        String n = packageName.substring(p.length() , packageName.indexOf(".", p.length()));
        System.out.println(p + n);
    }
}
