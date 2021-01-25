package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.predictor.PredictionService;
import org.hisp.dhis.predictor.PredictionSummary;
import org.hisp.dhis.predictor.PredictionTask;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.scheduling.JobConfiguration;
import org.hisp.dhis.scheduling.SchedulingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.jobConfigurationReport;
import static org.hisp.dhis.scheduling.JobType.PREDICTOR;

/**
 * @author Jim Grace
 */

@Controller
@RequestMapping( value = PredictionController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class PredictionController
{
    public static final String RESOURCE_PATH = "/predictions";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SchedulingManager schedulingManager;

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private WebMessageService webMessageService;

    @Autowired
    private RenderService renderService;

    @RequestMapping( method = { RequestMethod.PUT, RequestMethod.POST } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PREDICTOR_RUN')" )
    public void runPredictors(
        @RequestParam Date startDate,
        @RequestParam Date endDate,
        @RequestParam( value = "predictor", required = false ) List<String> predictors,
        @RequestParam( value = "predictorGroup", required = false ) List<String> predictorGroups,
        @RequestParam( defaultValue = "false", required = false ) boolean async,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        if ( async )
        {
            JobConfiguration jobId = new JobConfiguration( "inMemoryPrediction", PREDICTOR, currentUserService.getCurrentUser().getUid(), true );

            schedulingManager.executeJob( new PredictionTask( startDate, endDate, predictors, predictorGroups, predictionService, jobId ) );

            response.setHeader( "Location", ContextUtils.getRootPath( request ) + "/system/tasks/" + PREDICTOR );

            webMessageService.send( jobConfigurationReport( jobId ), response, request );
        }
        else
        {
            PredictionSummary predictionSummary = predictionService.predictTask( startDate, endDate, predictors, predictorGroups, null );

            renderService.toJson( response.getOutputStream(), predictionSummary );
        }
    }
}
