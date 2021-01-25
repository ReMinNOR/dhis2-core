package org.hisp.dhis.webapi.controller.indicator;



import org.hisp.dhis.analytics.resolver.ExpressionResolver;
import org.hisp.dhis.dxf2.webmessage.DescriptiveWebMessage;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.ExpressionValidationOutcome;
import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.schema.descriptors.IndicatorSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hisp.dhis.expression.ParseType.INDICATOR_EXPRESSION;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = IndicatorSchemaDescriptor.API_ENDPOINT )
public class IndicatorController
    extends AbstractCrudController<Indicator>
{
    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private ExpressionResolver resolver;

    @Autowired
    private I18nManager i18nManager;

    @RequestMapping( value = "/expression/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getExpressionDescription( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        String resolvedExpression = resolver.resolve( expression );
        ExpressionValidationOutcome result = expressionService.expressionIsValid( resolvedExpression,
            INDICATOR_EXPRESSION );

        DescriptiveWebMessage message = new DescriptiveWebMessage();
        message.setStatus( result.isValid() ? Status.OK : Status.ERROR );
        message.setMessage( i18n.getString( result.getKey() ) );

        if ( result.isValid() )
        {
            message.setDescription(
                expressionService.getExpressionDescription( resolvedExpression, INDICATOR_EXPRESSION ) );
        }

        webMessageService.sendJson( message, response );
    }
}
