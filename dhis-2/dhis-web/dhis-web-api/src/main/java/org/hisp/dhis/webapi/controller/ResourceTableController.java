package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.analytics.AnalyticsTableType;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.scheduling.parameters.AnalyticsJobParameters;
import org.hisp.dhis.scheduling.parameters.MonitoringJobParameters;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.jobConfigurationReport;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ResourceTableController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ResourceTableController
{
    public static final String RESOURCE_PATH = "/resourceTables";

    @Autowired
    private SchedulingManager schedulingManager;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private WebMessageService webMessageService;

    @RequestMapping( value = "/analytics", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public void analytics(
        @RequestParam( required = false ) boolean skipResourceTables,
        @RequestParam( required = false ) boolean skipAggregate,
        @RequestParam( required = false ) boolean skipEvents,
        @RequestParam( required = false ) boolean skipEnrollment,
        @RequestParam( required = false ) Integer lastYears,
        HttpServletResponse response, HttpServletRequest request )
    {
        Set<AnalyticsTableType> skipTableTypes = new HashSet<>();

        if ( skipAggregate )
        {
            skipTableTypes.add( AnalyticsTableType.DATA_VALUE );
            skipTableTypes.add( AnalyticsTableType.COMPLETENESS );
            skipTableTypes.add( AnalyticsTableType.COMPLETENESS_TARGET );
        }

        if ( skipEvents )
        {
            skipTableTypes.add( AnalyticsTableType.EVENT );
        }

        if ( skipEnrollment )
        {
            skipTableTypes.add( AnalyticsTableType.ENROLLMENT );
        }

        AnalyticsJobParameters analyticsJobParameters = new AnalyticsJobParameters( lastYears, skipTableTypes, skipResourceTables );

        JobConfiguration analyticsTableJob = new JobConfiguration( "inMemoryAnalyticsJob", JobType.ANALYTICS_TABLE, "",
            analyticsJobParameters, true, true );
        analyticsTableJob.setUserUid( currentUserService.getCurrentUser().getUid() );

        schedulingManager.executeJob( analyticsTableJob );

        webMessageService.send( jobConfigurationReport( analyticsTableJob ), response, request );
    }

    @RequestMapping( method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public void resourceTables( HttpServletResponse response, HttpServletRequest request )
    {
        JobConfiguration resourceTableJob = new JobConfiguration( "inMemoryResourceTableJob",
            JobType.RESOURCE_TABLE, currentUserService.getCurrentUser().getUid(), true );

        schedulingManager.executeJob( resourceTableJob );

        webMessageService.send( jobConfigurationReport( resourceTableJob ), response, request );
    }

    @RequestMapping( value = "/monitoring", method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    public void monitoring( HttpServletResponse response, HttpServletRequest request )
    {
        JobConfiguration monitoringJob = new JobConfiguration( "inMemoryMonitoringJob", JobType.MONITORING, "",
            new MonitoringJobParameters(), true, true );

        schedulingManager.executeJob( monitoringJob );

        webMessageService.send( jobConfigurationReport( monitoringJob ), response, request );
    }
}
