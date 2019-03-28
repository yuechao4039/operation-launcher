package com.dld.hll.financial.launcher;


import com.dld.hll.financial.core.util.DataMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
@RequestMapping(value = {"/api"})
public class ApiBaseController extends BaseController {

    /**
     * 总账
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/gl/*/*"}, produces = {"application/json"})
    public DataMap execute(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * 报表
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/report/*/*"}, produces = {"application/json"})
    public DataMap report(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * apm
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/auth/*/*"}, produces = {"application/json"})
    public DataMap auth(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }


    /**
     * apm
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/apm/*/*"}, produces = {"application/json"})
    public DataMap apm(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * tenant/frm
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/tenant/*/*"}, produces = {"application/json"})
    public DataMap tenant(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * svc
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/svc/*/*"}, produces = {"application/json"})
    public DataMap svc(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * aBase
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/aBase/*/*"}, produces = {"application/json"})
    public DataMap aBase(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * attachment
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/attachment/*/*"}, produces = {"application/json"})
    public DataMap attachment(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * assets 固定资产
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/assets/*/*"}, produces = {"application/json"})
    public DataMap assets(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * arp 应收应付
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/arp/*/*"}, produces = {"application/json"})
    public DataMap arp(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * arp 出纳
     *
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/cashier/*/*"}, produces = {"application/json"})
    public DataMap cashier(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }


    /**
     * doctr 凭证抛转
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/doctr/*/*"}, produces = {"application/json"})
    public DataMap doctr(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * trddoc 中间库
     * @param httpReq
     * @param httpResponse
     * @return
     */
    @RequestMapping(value = {"/trddoc/*/*"}, produces = {"application/json"})
    public DataMap trddoc(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    /**
     * task 任务库
     */
    @RequestMapping(value = {"/task/**/*"}, produces = {"application/json"})
    public DataMap task(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }

    @RequestMapping(value = {"/cloudTenant/*/*"}, produces = {"application/json"})
    public DataMap cloudTenant(HttpServletRequest httpReq, HttpServletResponse httpResponse) {
        DataMap baseResp = super.exec(httpReq, httpResponse);
        return baseResp;
    }
}

