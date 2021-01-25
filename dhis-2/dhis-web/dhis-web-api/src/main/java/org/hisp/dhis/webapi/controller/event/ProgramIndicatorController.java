package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.dxf2.webmessage.DescriptiveWebMessage;
import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramIndicatorService;
import org.hisp.dhis.schema.descriptors.ProgramIndicatorSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ProgramIndicatorSchemaDescriptor.API_ENDPOINT )
public class ProgramIndicatorController
    extends AbstractCrudController<ProgramIndicator>
{
    @Autowired
    private ProgramIndicatorService programIndicatorService;

    @Autowired
    private I18nManager i18nManager;

    @RequestMapping( value = "/expression/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void getExpressionDescription( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        DescriptiveWebMessage message = new DescriptiveWebMessage();

        try
        {
            message.setDescription( programIndicatorService.getExpressionDescription( expression ) );

            message.setStatus( Status.OK );

            message.setMessage( i18n.getString( ProgramIndicator.VALID ) );
        }
        catch ( IllegalStateException e )
        {
            message.setDescription( e.getMessage() );

            message.setStatus( Status.ERROR );

            message.setMessage( i18n.getString( ProgramIndicator.EXPRESSION_NOT_VALID ) );
        }

        webMessageService.sendJson( message, response );
    }

    @RequestMapping( value = "/filter/description", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void validateFilter( @RequestBody String expression, HttpServletResponse response )
        throws IOException
    {
        I18n i18n = i18nManager.getI18n();

        DescriptiveWebMessage message = new DescriptiveWebMessage();

        try
        {
            message.setDescription( programIndicatorService.getFilterDescription( expression ) );

            message.setStatus( Status.OK );

            message.setMessage( i18n.getString( ProgramIndicator.VALID ) );
        }
        catch ( IllegalStateException e )
        {
            message.setDescription( e.getMessage() );

            message.setStatus( Status.ERROR );

            message.setMessage( i18n.getString( ProgramIndicator.EXPRESSION_NOT_VALID ) );
        }

        webMessageService.sendJson( message, response );
    }
}
