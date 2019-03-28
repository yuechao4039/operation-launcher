package com.dld.hll.financial.launcher.core;

import com.hualala.passport.client.interceptor.PassportConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author yuechao 2018/11/16
 */
@Configuration
@PropertySource(value = {"classpath:passport.properties"})
public class PassportConfiguration {


    @Value("${cookie_domain:#{null}}")
    private String cookie_domain;

    @Value("${login_url:#{null}}")
    private String login_url;

    @Value("${hosts:#{null}}")
    private String hosts;

    @Value("${ssoDomain:financial.hualala.com}")
    private String ssoDomain;

    @Value("${product_line:0}")
    private Integer product_line = 0;

    @Bean
    public PassportConfig passportConfig() {

        PassportConfig passportConfig = new PassportConfig();
        passportConfig.setCookie_domain(this.cookie_domain);
        passportConfig.setLogin_url(this.login_url);
        passportConfig.setHosts(this.hosts);
        passportConfig.setProduct_line(this.product_line);
        return passportConfig;
    }



}
