package org.hisp.dhis.webapi.controller.validation;



import com.google.common.collect.Lists;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dxf2.webmessage.DescriptiveWebMessage;
import org.hisp.dhis.expression.ExpressionService;
import org.hisp.dhis.expression.ExpressionValidationOutcome;
import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.query.QueryParserException;
import org.hisp.dhis.schema.descriptors.ValidationRuleSchemaDescriptor;
import org.hisp.dhis.validation.ValidationRule;
import org.hisp.dhis.validation.ValidationRuleService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.hisp.dhis.expression.ParseType.VALIDATION_RULE_EXPRESSION;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ValidationRuleSchemaDescriptor.API_ENDPOINT )
public class ValidationRuleController
    extends AbstractCrudController<ValidationRule>
{
    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private ValidationRuleService validationRuleService;

    @Autowired
    private ExpressionService expressionService;

    @Autowired
    private I18nManager i18nManager;

    @Override
    protected List<ValidationRule> getEntityList( WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders )
        throws QueryParserException
    {
        if ( options.contains( "dataSet" ) )
        {
            DataSet ds = dataSetService.getDataSet( options.get( "dataSet" ) );

            if ( ds == null )
            {
                return null;
            }

            return Lists.newArrayList( validationRuleService.getValidationRulesForDataSet( ds ) );
        }

        return super.getEntityList( metadata, options, filters, orders );
    }

    @RequestMapping( value = "/expression/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getExpressionDescription( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        ExpressionValidationOutcome result = expressionService.expressionIsValid( expression, VALIDATION_RULE_EXPRESSION );

        DescriptiveWebMessage message = new DescriptiveWebMessage();
        message.setStatus( result.isValid() ? Status.OK : Status.ERROR );
        message.setMessage( i18n.getString( result.getKey() ) );

        if ( result.isValid() )
        {
            message.setDescription( expressionService.getExpressionDescription( expression, VALIDATION_RULE_EXPRESSION ) );
        }

        webMessageService.sendJson( message, response );
    }
}
