package com.dld.hll.financial.launcher.core;

import com.alibaba.fastjson.JSONObject;
import com.dld.hll.financial.base.api.login.SessionVoRequest;
import com.dld.hll.financial.base.api.login.SessionVoService;
import com.dld.hll.financial.core.exhandler.HualalaException;
import com.dld.hll.financial.core.exhandler.MsgDefine;
import com.dld.hll.financial.core.iface.vo.RequestHeader;
import com.dld.hll.financial.core.iface.vo.SessionVo;
import com.dld.hll.financial.core.interoperability.ServiceProxyHolder;
import com.dld.hll.financial.core.prop.AppConfiguration;
import com.dld.hll.financial.core.util.UUIDUtil;
import com.dld.hll.financial.interfaces.auth.trdusermapping.TrdUserMappingService;
import com.dld.hll.financial.interfaces.auth.trdusermapping.vo.QueryTrdUserMappingByPropertiesRequest;
import com.dld.hll.financial.interfaces.auth.trdusermapping.vo.QueryTrdUserMappingByPropertiesResp;
import com.dld.hll.financial.interfaces.cloudtenant.TrdTenantMappingService;
import com.dld.hll.financial.interfaces.cloudtenant.vo.trd.tenant.mapping.QueryTrdTenantMappingByPropertiesRequest;
import com.dld.hll.financial.interfaces.cloudtenant.vo.trd.tenant.mapping.QueryTrdTenantMappingByPropertiesResp;
import com.hualala.commons.errorcode.BasicErrorCode;
import com.hualala.grpc.ProtoConverter;
import com.hualala.passport.UserServiceGrpc;
import com.hualala.passport.UserServiceOuterClass;
import com.hualala.passport.client.client.PassportGrpcClientConfiguration;
import com.hualala.passport.client.client.PassportProperties;
import com.hualala.passport.client.exception.PassportClientException;
import com.hualala.passport.client.interceptor.PassportConfig;
import com.hualala.passport.client.util.PassportClientUtil;
import com.hualala.passport.common.Common;
import com.hualala.passport.common.error.PassportErrorCode;
import com.hualala.passport.common.error.PassportSubErrorCode;
import com.hualala.passport.common.model.User;
import com.hualala.passport.common.util.PassportUtil;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;


/**
 * @author yuechao 2018/5/31
 */
@WebFilter(filterName = "sessionFilter", urlPatterns = {"/*"},
        initParams = {@WebInitParam(name = "登录接口1", value = ".*/login.htmls$"),
                @WebInitParam(name = "验证码2", value = ".*givcode.*"),
                @WebInitParam(name = "首页3", value = ".*/index.html$"),
                @WebInitParam(name = "登录页4", value = ".*/login.html$"),
                @WebInitParam(name = "健康检查5", value = "/health"),
                @WebInitParam(name = "域名6", value = "^/$"),
                @WebInitParam(name = "图标7", value = ".*/favicon.ico$"),
                @WebInitParam(name = "静态资源8", value = ".*static.*"),
                @WebInitParam(name = "静态资源9", value = ".*chunks.*"),
                @WebInitParam(name = "登录页枚举", value = ".*/api/apm/regDetail/queryIntegrators.htmls$")})
@Slf4j
public class SessionFilter implements Filter {

    // URI在集合里面，不进行session校验
    private static List<String> excludeUrlPatterns = new ArrayList<>();

    private static PassportGrpcClientConfiguration.PassportGrpcClient passportGrpcClient;

    private static PassportClientUtil passportClientUtil;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> pNames = filterConfig.getInitParameterNames();
        String contextPath = filterConfig.getServletContext().getContextPath();
        while (pNames.hasMoreElements()) {
            excludeUrlPatterns.add(contextPath + filterConfig.getInitParameter(pNames.nextElement()));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;

        /** session中有值，直接跳过 */
        if (!Objects.isNull(req.getSession().getAttribute(SessionVo.SESSION_VO))) {
            chain.doFilter(request, response);
            return;
        }

        /** 不校验会话URL */
        String uri = req.getRequestURI();
        for (String pattern : excludeUrlPatterns) {
            if (uri.matches(pattern)) {
                chain.doFilter(request, response);
                return;
            }
        }

        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(req
                .getSession().getServletContext());
        AppConfiguration appConfiguration = webApplicationContext.getBean(AppConfiguration.class);

        // 如果域名非 financial.hualala.com 结尾
        String serverName = req.getServerName();
        String host = req.getRemoteHost();
        if (!req.getServerName().endsWith(appConfiguration.getSsoDomain())) {
            forwardToLogin(req, rep, "尚未登录");

            return;
        }


        /** 从passport 获取用户信息 */
        User user = getPassportUser(req, rep);
        if (user == null) {
            return;
        }


        RequestHeader header = new RequestHeader();
        header.setTrcid(UUIDUtil.uuid());
        // tbl_frm_trd_tenant_mapping
        // 通过 groupID-trdTenantCode, groupLoginName-trdTenantLoginName, groupShortName-trdTenantName
        // tbl_frm_trd_tenant_mapping
        TrdTenantMappingService trdTenantMappingService = ServiceProxyHolder.getInstance().getProxy(TrdTenantMappingService.class);
        QueryTrdTenantMappingByPropertiesRequest queryTrdTenantMappingByPropertiesRequest = new QueryTrdTenantMappingByPropertiesRequest();
        queryTrdTenantMappingByPropertiesRequest.setHeader(header);
        queryTrdTenantMappingByPropertiesRequest.setTrdTenantCode(String.valueOf(user.getGroupId()));
        QueryTrdTenantMappingByPropertiesResp queryTrdTenantMappingByPropertiesResp = null;
        try {
            queryTrdTenantMappingByPropertiesResp = trdTenantMappingService.queryTrdTenantMappingByProperties(queryTrdTenantMappingByPropertiesRequest);
        } catch (HualalaException e) {
            log.error(e.getMessage(), e);
            forwardToLogin(req, rep, MsgDefine.getProperty("关联商户在财务系统不存在"));
            return;
        }

        // tbl_auth_trd_user_mapping
        // sourceTenantCode-groupID, sourceUserCode-id, trdUserLoginName-userId, trdUserName-userName
        // 查询userID,无则插入，有则继续
        TrdUserMappingService trdUserMappingService = ServiceProxyHolder.getInstance().getProxy(TrdUserMappingService.class);
        QueryTrdUserMappingByPropertiesRequest queryTrdUserMappingByPropertiesRequest = new QueryTrdUserMappingByPropertiesRequest();
        queryTrdUserMappingByPropertiesRequest.setHeader(header);
        queryTrdUserMappingByPropertiesRequest.setSourceTenantCode(String.valueOf(user.getGroupId()));
        queryTrdUserMappingByPropertiesRequest.setSourceUserCode(String.valueOf(user.getId()));
        queryTrdUserMappingByPropertiesRequest.setTenantID(queryTrdTenantMappingByPropertiesResp.getTenantID());
        queryTrdUserMappingByPropertiesRequest.setTrdUserLoginName(user.getUserId());
        queryTrdUserMappingByPropertiesRequest.setTrdUserName(user.getUserName());
        queryTrdUserMappingByPropertiesRequest.setRoleType(user.getRoleType());
        QueryTrdUserMappingByPropertiesResp queryTrdUserMappingByPropertiesResp = null;
        try {
            queryTrdUserMappingByPropertiesResp = trdUserMappingService.queryTrdUserMappingByProperties(queryTrdUserMappingByPropertiesRequest);
        } catch (Exception e) {
            log.error(e.getMessage(), e);

            forwardToLogin(req, rep, "初始化用户失败");
            return;
        }

        SessionVoRequest sessionVoRequest = new SessionVoRequest();
        sessionVoRequest.setHeader(header);
        sessionVoRequest.setLoginName(queryTrdUserMappingByPropertiesResp.getTrdUserLoginName());
        sessionVoRequest.setTenantLoginName(queryTrdTenantMappingByPropertiesResp.getTenantLoginName());
        sessionVoRequest.setSource(SessionVoRequest.SESSION_VO_REQUEST_SOURCE_PASSPORT);

        SessionVoService sessionVoService = webApplicationContext.getBean(SessionVoService.class);
        // sessionVoService.kickOther(sessionVoRequest);
        ((HttpServletRequest) request).getSession().setAttribute(SessionVo.SESSION_VO, sessionVoService.getSessionVo(sessionVoRequest));


        chain.doFilter(request, response);
    }


    private User getPassportUser(HttpServletRequest req, HttpServletResponse rep) throws IOException {
        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(req
                .getSession().getServletContext());

        PassportConfig passportConfig = webApplicationContext.getBean(PassportConfig.class);

        if (passportGrpcClient == null) {
            passportGrpcClient = new PassportGrpcClientConfiguration.PassportGrpcClient(passportConfig.getHosts());
        }

        if (passportClientUtil == null) {
            passportClientUtil = new PassportClientUtil();
            PassportProperties passportProperties = new PassportProperties();
            passportProperties.setCookie_domain(passportConfig.getCookie_domain());
            passportProperties.setLogin_url(passportConfig.getLogin_url());
            passportClientUtil.setPassportProperties(passportProperties);
        }

        UserServiceGrpc.UserServiceBlockingStub blockingStub = null;
        try {
            blockingStub = (UserServiceGrpc.UserServiceBlockingStub)
                    passportGrpcClient.getBlockingStub(UserServiceGrpc.class);
        } catch (Exception e) {
            log.error("没有正常获取到grpc的stub", e);
            throw new PassportClientException("没有正确获取到grpc的stub", e);
        }

        String tokenValue = passportClientUtil.getAccessToken(req);

        if (StringUtils.isEmpty(tokenValue)) {
            log.info("请求没有携带token");
            forwardToLogin(req, rep, "通用token失效");
            return null;
        }

        Integer product_line = passportConfig.getProduct_line();

        UserServiceOuterClass.AccessToken accessToken = UserServiceOuterClass.AccessToken.newBuilder()
                .setTokenValue(tokenValue).setProductLine(product_line).setHost(PassportUtil.getOrigin(req))
                .build();

        UserServiceOuterClass.AuthenticationResult authenticate = null;

        try {
            authenticate = blockingStub.authenticate(accessToken);
        } catch (StatusRuntimeException e) {
            log.error("interceptor getting user info failed, accessToken : {}, exception : " +
                    "{}", tokenValue, e);
            throw new PassportClientException(e);
        }

        Common.ResponseHeader responseHeader = authenticate.getResponseHeader();
        if (responseHeader.getCode().equals(BasicErrorCode.SUCCESS.getCode())) {
            User user = ProtoConverter.messageToBean(authenticate.getUser(), User.class);

            return user;
        } else if (responseHeader.getCode().equals(PassportErrorCode.ACCESS_TOKEN_INVALID.getCode())) {
            log.info("token验证失败，token:{}", tokenValue);
            forwardToLogin(req, rep, "token失效");
            return null;
        } else {
            throw new PassportClientException("passport interceptor inner exception， should not " +
                    "reach here.");
        }
    }


    public void forwardToLogin(HttpServletRequest request, HttpServletResponse response, String msg)
            throws IOException {
        String redirectUrl = null;
        String host = URLEncoder.encode(("http://" + request.getHeader("host")), "UTF-8");

        WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(request
                .getSession().getServletContext());
        AppConfiguration appConfiguration = webApplicationContext.getBean(AppConfiguration.class);

        if (request.getServerName().endsWith(appConfiguration.getSsoDomain())) {
            if (request.getServerName().contains("dohko")) {
                redirectUrl = "http://dohko.login.hualala.com/login?redirectURL=" + host;
            } else {
                redirectUrl = "http://passport.hualala.com/login?redirectURL=" + host;
            }
        } else {
            redirectUrl = "http://" + request.getHeader("host") + "/login.html";
        }
        if (checkAjax(request)) {
            // 如果是ajax请求的话，返回redirect的response
            JSONObject redirectJsonObject = new JSONObject();
            redirectJsonObject.put("redirectUrl", redirectUrl);
            redirectJsonObject.put("fMsg", msg);
            String result = PassportUtil.generateResultFromPassportSubErrorCode(PassportSubErrorCode.COMMON_TOKEN_INVALID, redirectJsonObject);

            writeResponse(response, result);
        } else {
            response.sendRedirect(redirectUrl);
            setCorsHeader(response);
        }
    }

    private void writeResponse(HttpServletResponse response, String result) throws IOException {
        response.setContentType("application/json;" + "charset=UTF-8");
        PrintWriter responseWriter = response.getWriter();
        responseWriter.print(result);
        responseWriter.flush();
    }

    public static void setCorsHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
    }

    private boolean checkAjax(HttpServletRequest request) {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroy() {

    }
}
