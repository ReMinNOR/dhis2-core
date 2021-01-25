package org.hisp.dhis.webapi.controller;



import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.pushanalysis.PushAnalysis;
import org.hisp.dhis.pushanalysis.PushAnalysisService;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.scheduling.parameters.PushAnalysisJobParameters;
import org.hisp.dhis.schema.descriptors.PushAnalysisSchemaDescriptor;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Stian Sandvold
 */
@Controller
@RequestMapping( PushAnalysisSchemaDescriptor.API_ENDPOINT )
@Slf4j
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class PushAnalysisController
    extends AbstractCrudController<PushAnalysis>
{
    @Autowired
    private PushAnalysisService pushAnalysisService;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SchedulingManager schedulingManager;

    @RequestMapping( value = "/{uid}/render", method = RequestMethod.GET )
    public void renderPushAnalytics( @PathVariable( ) String uid, HttpServletResponse response )
        throws WebMessageException,
        IOException
    {
        PushAnalysis pushAnalysis = pushAnalysisService.getByUid( uid );

        if ( pushAnalysis == null )
        {
            throw new WebMessageException(
                WebMessageUtils.notFound( "Push analysis with uid " + uid + " was not found" ) );
        }

        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_HTML, CacheStrategy.NO_CACHE );

        log.info( "User '" + currentUserService.getCurrentUser().getUsername() + "' started PushAnalysis for 'rendering'" );

        String result = pushAnalysisService.generateHtmlReport( pushAnalysis, currentUserService.getCurrentUser(), null );
        response.getWriter().write( result );
        response.getWriter().close();
    }

    @ResponseStatus( HttpStatus.NO_CONTENT )
    @RequestMapping( value = "/{uid}/run", method = RequestMethod.POST )
    public void sendPushAnalysis( @PathVariable() String uid ) throws WebMessageException, IOException
    {
        PushAnalysis pushAnalysis = pushAnalysisService.getByUid( uid );

        if ( pushAnalysis == null )
        {
            throw new WebMessageException(
                WebMessageUtils.notFound( "Push analysis with uid " + uid + " was not found" ) );
        }

        JobConfiguration pushAnalysisJobConfiguration = new JobConfiguration( "pushAnalysisJob from controller",
            JobType.PUSH_ANALYSIS, "", new PushAnalysisJobParameters( uid ), true, true );
        schedulingManager.executeJob( pushAnalysisJobConfiguration );
    }
}
