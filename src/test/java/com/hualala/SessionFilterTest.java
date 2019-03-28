package com.hualala;

import org.junit.Test;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import java.util.ArrayList;
import java.util.List;



public class SessionFilterTest  {
    @Test
    public void matchTest() {
        List<String> patterns = new ArrayList<>();
        patterns.add(".*/login.htmls$");
        patterns.add(".*givcode.*");
        patterns.add(".*/index.html$");
        patterns.add(".*/login.html$");
        patterns.add("/health");
        patterns.add("^/$");
        patterns.add(".*/favicon.ico$");
        patterns.add(".*static.*");
        patterns.add(".*chunks.*");


        String uri = "/api/bebase/givcode.html";
        for (int i = 0; i < patterns.size(); i++) {
            System.out.println(uri.matches(patterns.get(i)));
        }

    }

}
