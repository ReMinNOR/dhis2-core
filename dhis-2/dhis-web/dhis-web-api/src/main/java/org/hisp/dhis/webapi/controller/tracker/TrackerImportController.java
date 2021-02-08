/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.webapi.controller.tracker;

import static org.hisp.dhis.webapi.controller.tracker.TrackerControllerSupport.RESOURCE_PATH;
import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

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
    // @PreAuthorize( "hasRole('ALL') or
    // hasRole('F_TRACKER_IMPORTER_EXPERIMENTAL')" )
    public void asyncPostJsonTracker( HttpServletRequest request, HttpServletResponse response, User currentUser,
        @RequestBody TrackerBundleParams trackerBundleParams )
        throws IOException
    {

        String jobId = trackerMessageManager.addJob(
            buildTrackerImportParams( request, currentUser, trackerBundleParams ) );

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
    // @PreAuthorize( "hasRole('ALL') or
    // hasRole('F_TRACKER_IMPORTER_EXPERIMENTAL')" )
    public TrackerImportReport syncPostJsonTracker(
        @RequestParam( defaultValue = "full", required = false ) String reportMode,
        HttpServletRequest request, User currentUser, @RequestBody TrackerBundleParams trackerBundleParams )
    {

        TrackerBundleReportMode trackerBundleReportMode = getTrackerBundleReportMode( reportMode );

        TrackerImportParams trackerImportParams = buildTrackerImportParams( request, currentUser, trackerBundleParams );
        TrackerImportReport trackerImportReport = trackerImportService.importTracker( trackerImportParams );

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
    private TrackerImportParams buildTrackerImportParams( HttpServletRequest request, User currentUser,
        TrackerBundleParams trackerBundleParams )
    {
        TrackerImportParams.TrackerImportParamsBuilder paramsBuilder = TrackerImportParamsBuilder
            .builder( contextService.getParameterValuesMap() );

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