package org.hisp.dhis.webapi.controller.sms;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.message.MessageSender;
import org.hisp.dhis.outboundmessage.OutboundMessageResponse;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.sms.outbound.OutboundSms;
import org.hisp.dhis.sms.outbound.OutboundSmsService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.notFound;

/**
 * @author Zubair Asghar
 */

@RestController
@RequestMapping( value = "/sms/outbound" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class SmsOutboundController extends AbstractCrudController<OutboundSms>
{
    private final MessageSender smsSender;
    private final WebMessageService webMessageService;
    private final RenderService renderService;
    private final OutboundSmsService outboundSmsService;

    public SmsOutboundController(
            @Qualifier( "smsMessageSender" ) MessageSender smsSender,
            WebMessageService webMessageService,
            RenderService renderService,
            CurrentUserService currentUserService,
            OutboundSmsService outboundSmsService )
    {
        this.smsSender = smsSender;
        this.webMessageService = webMessageService;
        this.renderService = renderService;
        this.currentUserService = currentUserService;
        this.outboundSmsService = outboundSmsService;
    }

    // -------------------------------------------------------------------------
    // POST
    // -------------------------------------------------------------------------

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( method = RequestMethod.POST, produces = { "application/json" } )
    public void sendSMSMessage( @RequestParam String recipient, @RequestParam String message,
        HttpServletResponse response, HttpServletRequest request ) throws WebMessageException
    {
        if ( recipient == null || recipient.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Recipient must be specified" ) );
        }

        if ( message == null || message.length() <= 0 )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Message must be specified" ) );
        }

        OutboundMessageResponse status = smsSender.sendMessage( null, message, recipient );

        if ( status.isOk() )
        {
            webMessageService.send( WebMessageUtils.ok( "SMS sent" ), response, request );
        }
        else
        {
            throw new WebMessageException( WebMessageUtils.error( status.getDescription() ) );
        }
    }

    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SENDSMS')" )
    @RequestMapping( method = RequestMethod.POST, consumes = { "application/json" }, produces = { "application/json" } )
    public void sendSMSMessage( HttpServletResponse response, HttpServletRequest request )
            throws WebMessageException, IOException
    {
        OutboundSms sms = renderService.fromJson( request.getInputStream(), OutboundSms.class );

        OutboundMessageResponse status = smsSender.sendMessage( null, sms.getMessage(), sms.getRecipients() );

        if ( status.isOk() )
        {
            webMessageService.send( WebMessageUtils.ok( "SMS sent" ), response, request );
        }
        else
        {
            throw new WebMessageException( WebMessageUtils.error( status.getDescription() ) );
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/{uid}", method = RequestMethod.DELETE, produces = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SETTINGS')" )
    public void deleteOutboundMessage(@PathVariable String uid, HttpServletRequest request, HttpServletResponse response )
            throws WebMessageException
    {
        OutboundSms sms = outboundSmsService.get( uid );

        if ( sms == null )
        {
            throw new WebMessageException( notFound( "No OutboundSms with id '" + uid + "' was found." ) );
        }

        outboundSmsService.delete( uid );

        webMessageService.send( WebMessageUtils.ok( "OutboundSms with "+ uid + " deleted" ), response, request );
    }

    @RequestMapping( method = RequestMethod.DELETE, produces = "application/json" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_MOBILE_SETTINGS')" )
    public void deleteOutboundMessages( @RequestParam List<String> ids, HttpServletRequest request, HttpServletResponse response )
    {
        ids.forEach( outboundSmsService::delete );

        webMessageService.send( WebMessageUtils.ok( "Objects deleted" ), response, request );
    }
}
