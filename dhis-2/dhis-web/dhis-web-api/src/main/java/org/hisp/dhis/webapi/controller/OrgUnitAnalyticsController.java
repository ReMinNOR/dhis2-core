package org.hisp.dhis.webapi.controller;



import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.analytics.orgunit.OrgUnitQueryParams;
import org.hisp.dhis.analytics.orgunit.OrgUnitAnalyticsService;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lars Helge Overland
 */
@Controller
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class OrgUnitAnalyticsController
{
    private static final String RESOURCE_PATH = "/orgUnitAnalytics";

    @Autowired
    private OrgUnitAnalyticsService analyticsService;

    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( value = RESOURCE_PATH, method = RequestMethod.GET, produces = { "application/json" } )
    public @ResponseBody Grid getJson(
        @RequestParam String ou,
        @RequestParam String ougs,
        @RequestParam( required = false ) String columns,
        DhisApiVersion apiVersion,
        HttpServletResponse response ) throws Exception
    {
        OrgUnitQueryParams params = analyticsService.getParams( ou, ougs, columns );
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JSON, CacheStrategy.RESPECT_SYSTEM_SETTING );
        return analyticsService.getOrgUnitData( params );
    }

    @RequestMapping( value = RESOURCE_PATH + ".xls", method = RequestMethod.GET )
    public void getXls(
        @RequestParam String ou,
        @RequestParam String ougs,
        @RequestParam( required = false ) String columns,
        DhisApiVersion apiVersion,
        HttpServletResponse response ) throws Exception
    {
        OrgUnitQueryParams params = analyticsService.getParams( ou, ougs, columns );
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_EXCEL, CacheStrategy.RESPECT_SYSTEM_SETTING );
        Grid grid = analyticsService.getOrgUnitData( params );
        GridUtils.toXls( grid, response.getOutputStream() );
    }

    @RequestMapping( value = RESOURCE_PATH + ".csv", method = RequestMethod.GET )
    public void getCsv(
        @RequestParam String ou,
        @RequestParam String ougs,
        @RequestParam( required = false ) String columns,
        DhisApiVersion apiVersion,
        HttpServletResponse response ) throws Exception
    {
        OrgUnitQueryParams params = analyticsService.getParams( ou, ougs, columns );
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, CacheStrategy.RESPECT_SYSTEM_SETTING );
        Grid grid = analyticsService.getOrgUnitData( params );
        GridUtils.toCsv( grid, response.getWriter() );
    }

    @RequestMapping( value = RESOURCE_PATH + ".pdf", method = RequestMethod.GET )
    public void getPdf(
        @RequestParam String ou,
        @RequestParam String ougs,
        @RequestParam( required = false ) String columns,
        DhisApiVersion apiVersion,
        HttpServletResponse response ) throws Exception
    {
        OrgUnitQueryParams params = analyticsService.getParams( ou, ougs, columns );
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, CacheStrategy.RESPECT_SYSTEM_SETTING );
        Grid grid = analyticsService.getOrgUnitData( params );
        GridUtils.toPdf( grid, response.getOutputStream() );
    }
}
