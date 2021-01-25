package org.hisp.dhis.webapi.controller;



import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.dxf2.common.TranslateParams;
import org.hisp.dhis.dxf2.webmessage.DescriptiveWebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.ExpressionValidationOutcome;
import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.predictor.PredictionService;
import org.hisp.dhis.predictor.PredictionSummary;
import org.hisp.dhis.predictor.Predictor;
import org.hisp.dhis.predictor.PredictorService;
import org.hisp.dhis.schema.descriptors.PredictorSchemaDescriptor;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.hisp.dhis.expression.ParseType.*;

/**
 * @author Ken Haase (ken@dhis2.org)
 */
@Controller
@Slf4j
@RequestMapping( value = PredictorSchemaDescriptor.API_ENDPOINT )
public class PredictorController
    extends AbstractCrudController<Predictor>
{
    @Autowired
    private PredictorService predictorService;

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private WebMessageService webMessageService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private I18nManager i18nManager;

    @RequestMapping( value = "/{uid}/run", method = { RequestMethod.POST, RequestMethod.PUT } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PREDICTOR_RUN')" )
    public void runPredictor(
        @PathVariable( "uid" ) String uid,
        @RequestParam Date startDate,
        @RequestParam Date endDate,
        TranslateParams translateParams,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        Predictor predictor = predictorService.getPredictor( uid );

        try
        {
            PredictionSummary predictionSummary = new PredictionSummary();

            predictionService.predict( predictor, startDate, endDate, predictionSummary );

            webMessageService.send( WebMessageUtils.ok( "Generated " + predictionSummary.getPredictions() + " predictions" ), response, request );
        }
        catch ( Exception ex )
        {
            log.error( "Unable to predict " + predictor.getName(), ex );

            webMessageService.send( WebMessageUtils.conflict( "Unable to predict " + predictor.getName(), ex.getMessage() ), response, request );
        }
    }

    @RequestMapping( value = "/run", method = { RequestMethod.POST, RequestMethod.PUT } )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_PREDICTOR_RUN')" )
    public void runPredictors(
        @RequestParam Date startDate,
        @RequestParam Date endDate,
        TranslateParams translateParams,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        int count = 0;

        List<Predictor> allPredictors = predictorService.getAllPredictors();

        for ( Predictor predictor : allPredictors )
        {
            try
            {
                PredictionSummary predictionSummary = new PredictionSummary();

                predictionService.predict( predictor, startDate, endDate, predictionSummary );

                count += predictionSummary.getPredictions();
            }
            catch ( Exception ex )
            {
                log.error( "Unable to predict " + predictor.getName(), ex );

                webMessageService.send( WebMessageUtils.conflict( "Unable to predict " + predictor.getName(), ex.getMessage() ), response, request );

                return;
            }
        }

        webMessageService.send( WebMessageUtils.ok( "Generated " + count + " predictions" ), response, request );
    }

    @RequestMapping( value = "/expression/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getExpressionDescription( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        ExpressionValidationOutcome result = expressionService.expressionIsValid( expression, PREDICTOR_EXPRESSION );

        DescriptiveWebMessage message = new DescriptiveWebMessage();
        message.setStatus( result.isValid() ? Status.OK : Status.ERROR );
        message.setMessage( i18n.getString( result.getKey() ) );

        if ( result.isValid() )
        {
            message.setDescription( expressionService.getExpressionDescription( expression, PREDICTOR_EXPRESSION ) );
        }

        webMessageService.sendJson( message, response );
    }

    @RequestMapping( value = "/skipTest/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getSkipTestDescription( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        ExpressionValidationOutcome result = expressionService.expressionIsValid( expression, PREDICTOR_SKIP_TEST );

        DescriptiveWebMessage message = new DescriptiveWebMessage();
        message.setStatus( result.isValid() ? Status.OK : Status.ERROR );
        message.setMessage( i18n.getString( result.getKey() ) );

        if ( result.isValid() )
        {
            message.setDescription( expressionService.getExpressionDescription( expression, PREDICTOR_SKIP_TEST ) );
        }

        webMessageService.sendJson( message, response );
    }
}
