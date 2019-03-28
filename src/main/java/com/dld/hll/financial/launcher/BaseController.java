package com.dld.hll.financial.launcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dld.hll.financial.core.aop.ApiAutoAssignValuesAspect;
import com.dld.hll.financial.core.api.RequestParameterSupport;
import com.dld.hll.financial.core.exhandler.HualalaException;
import com.dld.hll.financial.core.iface.OperationBgSessionIface;
import com.dld.hll.financial.core.iface.SessionIface;
import com.dld.hll.financial.core.iface.vo.BaseReq;
import com.dld.hll.financial.core.iface.vo.BaseResp;
import com.dld.hll.financial.core.iface.vo.RequestHeader;
import com.dld.hll.financial.core.iface.vo.SessionVo;
import com.dld.hll.financial.core.interoperability.ServiceProxy;
import com.dld.hll.financial.core.schema.FilterBean;
import com.dld.hll.financial.core.util.DataMap;
import com.dld.hll.financial.core.util.DataMapUtil;
import com.dld.hll.financial.core.util.HttpHolderUtil;
import com.dld.hll.financial.core.util.IpAddrUtil;
import com.dld.hll.financial.interfaces.auth.UserDataRightService;
import com.dld.hll.financial.interfaces.auth.vo.user.data.right.UserDataQueryValueReq;
import com.dld.hll.financial.interfaces.auth.vo.user.data.right.UserDataQueryValueResp;
import com.dld.hll.financial.interfaces.tenant.TenantAccountService;
import com.dld.hll.financial.interfaces.tenant.vo.tenant.account.QueryAccountIDsByTenantIDResp;
import com.dld.hll.financial.interfaces.tenant.vo.tenant.account.TenantAccountQueryByTenantIDReq;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

@Slf4j
public class BaseController implements ApplicationContextAware {

    @Autowired
    protected ApplicationContext appContext;

    @Autowired
    protected RequestParameterSupport requestParameterSupport;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.appContext = applicationContext;
    }

    @Autowired
    protected ServiceProxy serviceProxy;


    public DataMap exec(HttpServletRequest request, HttpServletResponse httpResponse) {
        long startTime = System.currentTimeMillis();
        log.info("contextPath={}, requestURI={}, method={}, contentType={}", request.getContextPath(), request.getRequestURI(), request.getMethod(), request.getContentType());
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());
        if (!this.appContext.containsBean(requestURI)) {
            // SER_C_100016=请求路径未配置
            throw new HualalaException("SER_C_100016");
        }
        FilterBean filterBean = this.appContext.getBean(requestURI, FilterBean.class);

        Class<?> cls = ClassUtils.resolveClassName(filterBean.getService(), ClassUtils.getDefaultClassLoader());
        if (!cls.isInterface()) {
            // SER_C_100017=服务必须是接口
            throw new HualalaException("SER_C_100017");
        }
        // 查找service的对应方法
        Optional<Method> optional = Arrays.asList(ReflectionUtils.getAllDeclaredMethods(cls))
                .stream()
                .filter(me -> me.getName().equals(filterBean.getMethod()))
                .findFirst();
        if (!optional.isPresent()) {
            // SER_C_100020=方法不存在app
            throw new HualalaException("SER_C_100020");
        }
        Method method = optional.get();

        DataMap reqData = this.assembleRequest(request);

        DataMap integratedDataMap = this.requestParameterSupport.reqDataFilter(filterBean.getRequest(), reqData);

        BaseReq baseReq = this.requestParameterSupport.dataMapToRequestBean(integratedDataMap, method.getParameterTypes()[0]);
        log.info("httpRequestParam=" + JSONObject.toJSONString(baseReq));


        /** 通用参数、会话参数 其中debug、trcid、accountID从前端传递  start **/
        baseReq.getHeader().setRemoteIp(IpAddrUtil.getRemoteIp(request));
        baseReq.getHeader().setRequestURI(request.getRequestURI());

        HttpSession session = HttpHolderUtil.getHttpSession(false);
        if (session != null) {
            SessionVo sessionVo = (SessionVo) session.getAttribute(SessionVo.SESSION_VO);
            // 如果方法首个入参实现了SessionIface接口，从session中取值，给req赋值
            if (SessionIface.class.isAssignableFrom(baseReq.getClass())) {
                SessionIface sessionIface = (SessionIface) baseReq;
                sessionIface.setUserID(sessionVo.getUserID());
                sessionIface.setUserName(sessionVo.getUserName());
                sessionIface.setTenantID(sessionVo.getTenantID());
            }
            if (OperationBgSessionIface.class.isAssignableFrom(baseReq.getClass())) {
                OperationBgSessionIface sessionIface = (OperationBgSessionIface) baseReq;
                sessionIface.setUserID(sessionVo.getUserID());
                sessionIface.setUserName(sessionVo.getUserName());
                sessionIface.setIsvID(sessionVo.getIsvID());
            }
        }
        ApiAutoAssignValuesAspect.setAutoDeliver(baseReq);
        /** 通用参数、会话参数  end **/

        Object obj = serviceProxy.getProxy(cls);
        BaseResp baseResp = (BaseResp) ReflectionUtils.invokeMethod(method, obj, baseReq);

        DataMap respDataMap = this.requestParameterSupport.resultBeanToDataMap(baseResp, method.getReturnType());

        DataMap dataMap = this.requestParameterSupport.resDataFilter(filterBean.getResponse(), reqData, (DataMap) respDataMap);

        log.info(requestURI + " http service invoke elapsed " + (System.currentTimeMillis() - startTime));

        return dataMap;
    }

    /**
     * @param request
     * @return
     */
    public DataMap assembleRequest(HttpServletRequest request) {

        if (StringUtils.isEmpty(request.getContentType())) {
            // SER_C_100018=mime类型不支持
            throw new HualalaException("SER_C_100018");
        }
        if (request.getContentType().startsWith(MimeTypeUtils.APPLICATION_JSON_VALUE)) {
            return getRequestPayLoad(request);
        } else {
            // SER_C_100018=mime类型不支持
            throw new HualalaException("SER_C_100018");
        }
    }

    private DataMap getRequestPayLoad(HttpServletRequest request) {
        DataMap dataMap = new DataMap();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String str;
            StringBuilder sb = new StringBuilder();
            while (null != (str = br.readLine())) {
                sb.append(str);
            }
            JSONObject jsonObject = JSON.parseObject(sb.toString());
            if (null == jsonObject) {
                return dataMap;
            }
            dataMap = DataMapUtil.toDataMap(jsonObject);
            if (null != dataMap.get("data") && dataMap.get("data") instanceof DataMap) {
                dataMap.putAll((DataMap) dataMap.get("data"));
                dataMap.remove("data");
            }

        } catch (IOException e) {
            log.error(e.getMessage(), e);
            // SER_C_100019=请求参数解析异常
            throw new HualalaException("SER_C_100019");
        }
        return dataMap;
    }
}

