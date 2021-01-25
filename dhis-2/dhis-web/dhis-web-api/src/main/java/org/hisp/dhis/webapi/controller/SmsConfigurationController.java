package org.hisp.dhis.webapi.controller;



import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.sms.config.GenericHttpGatewayConfig;
import org.hisp.dhis.sms.config.SmsConfiguration;
import org.hisp.dhis.sms.config.SmsConfigurationManager;
import org.hisp.dhis.sms.config.SmsGatewayConfig;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Controller
@RequestMapping( value = SmsConfigurationController.RESOURCE_PATH )
@Slf4j
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class SmsConfigurationController
{
    public static final String RESOURCE_PATH = "/config/sms";

    @Autowired
    private SmsConfigurationManager smsConfigurationManager;

    @RequestMapping( method = RequestMethod.GET )
    public @ResponseBody SmsConfiguration getSmsConfiguration()
    {
        SmsConfiguration smsConfiguration = smsConfigurationManager.getSmsConfiguration();

        if ( smsConfiguration == null )
        {
            smsConfiguration = new SmsConfiguration();
        }

        return smsConfiguration;
    }

    @RequestMapping( value = "test", method = RequestMethod.GET )
    public @ResponseBody SmsConfiguration getTest()
    {
        SmsConfiguration smsConfiguration = new SmsConfiguration();

        SmsGatewayConfig gatewayConfig = new GenericHttpGatewayConfig();
        gatewayConfig.setUrlTemplate( "http://storset.org/" );
        smsConfiguration.setGateways( Collections.singletonList( gatewayConfig ) );

        return smsConfiguration;
    }

    // --------------------------------------------------------------------------
    // POST
    // --------------------------------------------------------------------------

    @RequestMapping( method = RequestMethod.PUT )
    public @ResponseBody SmsConfiguration putSmsConfig( @RequestBody SmsConfiguration smsConfiguration )
        throws Exception
    {
        if ( smsConfiguration == null )
        {
            throw new IllegalArgumentException( "SMS configuration not set" );
        }

        smsConfigurationManager.updateSmsConfiguration( smsConfiguration );

        return getSmsConfiguration();
    }

    @ExceptionHandler
    public void mapException( IllegalArgumentException exception, HttpServletResponse response )
        throws IOException
    {
        log.info( "Exception", exception );
        response.setStatus( HttpServletResponse.SC_CONFLICT );
        response.setContentType( ContextUtils.CONTENT_TYPE_TEXT );
        response.getWriter().write( exception.getMessage() );
    }
}
