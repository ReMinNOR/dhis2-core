package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.jobConfigurationReport;

/**
 * @author Halvdan Hoem Grelland <halvdanhg@gmail.com>
 */
@Controller
@RequestMapping( method = RequestMethod.GET )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class DataIntegrityController
{
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SchedulingManager schedulingManager;

    @Autowired
    private WebMessageService webMessageService;

    public static final String RESOURCE_PATH = "/dataIntegrity";

    //--------------------------------------------------------------------------
    // Start asynchronous data integrity task
    //--------------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_PERFORM_MAINTENANCE')" )
    @RequestMapping( value = DataIntegrityController.RESOURCE_PATH, method = RequestMethod.POST )
    public void runAsyncDataIntegrity( HttpServletResponse response, HttpServletRequest request )
    {
        JobConfiguration jobConfiguration = new JobConfiguration( "runAsyncDataIntegrity", JobType.DATA_INTEGRITY, null, true );
        jobConfiguration.setUserUid( currentUserService.getCurrentUser().getUid() );
        jobConfiguration.setAutoFields();

        schedulingManager.executeJob( jobConfiguration );

        webMessageService.send( jobConfigurationReport( jobConfiguration ), response, request );
    }
}
