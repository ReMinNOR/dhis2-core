package org.hisp.dhis.webapi.controller.tracker;



import static org.hisp.dhis.webapi.controller.tracker.TrackerControllerSupport.RESOURCE_PATH;
import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.scheduling.JobType;
import org.hisp.dhis.system.notification.Notification;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.tracker.TrackerBundleReportMode;
import org.hisp.dhis.tracker.TrackerImportParams;
import org.hisp.dhis.tracker.TrackerImportService;
import org.hisp.dhis.tracker.job.TrackerJobWebMessageResponse;
import org.hisp.dhis.tracker.job.TrackerMessageManager;
import org.hisp.dhis.tracker.report.TrackerImportReport;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@RestController
@RequestMapping( value = RESOURCE_PATH )
@RequiredArgsConstructor
public class TrackerImportController
{
    static final String TRACKER_JOB_ADDED = "Tracker job added";

    private final TrackerImportService trackerImportService;

    private final RenderService renderService;

    private final ContextService contextService;

    private final TrackerMessageManager trackerMessageManager;

    private final Notifier notifier;

    @PostMapping( value = "", consumes = APPLICATION_JSON_VALUE )
    // @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKER_IMPORTER_EXPERIMENTAL')" )
    public void asyncPostJsonTracker( HttpServletRequest request, HttpServletResponse response, User currentUser )
        throws IOException
    {

        String jobId = trackerMessageManager.addJob(
            buildTrackerImportParams( request, currentUser ) );

        String location = ContextUtils.getRootPath( request ) + "/tracker/jobs/" + jobId;
        response.setHeader( "Location", location );
        response.setContentType( APPLICATION_JSON_VALUE );

        renderService.toJson( response.getOutputStream(), new WebMessage()
            .setMessage( TRACKER_JOB_ADDED )
            .setResponse(
                TrackerJobWebMessageResponse.builder()
                    .id( jobId ).location( location )
                    .build() ) );
    }

    @PostMapping( value = "", consumes = APPLICATION_JSON_VALUE, params = { "async=false" } )
    // @PreAuthorize( "hasRole('ALL') or hasRole('F_TRACKER_IMPORTER_EXPERIMENTAL')" )
    public TrackerImportReport syncPostJsonTracker(
        @RequestParam( defaultValue = "full", required = false ) String reportMode,
        HttpServletRequest request, User currentUser )
    {

        TrackerBundleReportMode trackerBundleReportMode = getTrackerBundleReportMode( reportMode );

        TrackerImportReport trackerImportReport = trackerImportService.importTracker(
            buildTrackerImportParams( request, currentUser ) );

        return trackerImportService.buildImportReport( trackerImportReport, trackerBundleReportMode );

    }

    private TrackerBundleReportMode getTrackerBundleReportMode( String reportMode )
    {

        TrackerBundleReportMode trackerBundleReportMode;
        try
        {
            trackerBundleReportMode = TrackerBundleReportMode.valueOf( reportMode.toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            throw new HttpClientErrorException( HttpStatus.BAD_REQUEST,
                "Value " + reportMode + " is not a valid report mode" );
        }
        return trackerBundleReportMode;
    }

    @SneakyThrows
    private TrackerImportParams buildTrackerImportParams( HttpServletRequest request, User currentUser )
    {
        TrackerImportParams.TrackerImportParamsBuilder paramsBuilder = TrackerImportParamsBuilder.builder( contextService.getParameterValuesMap() );

        TrackerBundleParams trackerBundleParams = renderService.fromJson(
                request.getInputStream(),
                TrackerBundleParams.class );

        return paramsBuilder
                .userId( currentUser.getUid() )
                .trackedEntities( trackerBundleParams.getTrackedEntities() )
                .enrollments( trackerBundleParams.getEnrollments() )
                .events( trackerBundleParams.getEvents() )
                .relationships( trackerBundleParams.getRelationships() )
                .build();
    }

    @GetMapping( value = "/jobs/{uid}", produces = APPLICATION_JSON_VALUE )
    public List<Notification> getJob( @PathVariable String uid, HttpServletResponse response )
        throws HttpStatusCodeException
    {
        List<Notification> notifications = notifier.getNotificationsByJobId( JobType.TRACKER_IMPORT_JOB, uid );
        setNoStore( response );

        return notifications;
    }

    @GetMapping( value = "/jobs/{uid}/report", produces = APPLICATION_JSON_VALUE )
    public TrackerImportReport getJobReport( @PathVariable String uid,
        @RequestParam( defaultValue = "errors", required = false ) String reportMode,
        HttpServletResponse response )
        throws HttpStatusCodeException
    {
        TrackerBundleReportMode trackerBundleReportMode = getTrackerBundleReportMode( reportMode );

        Object importReport = notifier.getJobSummaryByJobId( JobType.TRACKER_IMPORT_JOB, uid );
        setNoStore( response );

        if ( importReport != null )
        {
            return trackerImportService.buildImportReport( (TrackerImportReport) importReport,
                trackerBundleReportMode );
        }

        throw new HttpClientErrorException( HttpStatus.NOT_FOUND );
    }
}
