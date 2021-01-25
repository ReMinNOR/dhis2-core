package org.hisp.dhis.webapi.controller.security;



import com.fasterxml.jackson.databind.ObjectMapper;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.security.SecurityUtils;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henning Håkonsen
 */
@RestController
@RequestMapping( value = "/2fa" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class SecurityController
{
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private WebMessageService webMessageService;

    @Autowired
    private ObjectMapper jsonMapper;

    @RequestMapping( value = "/qr", method = RequestMethod.GET, produces = "application/json" )
    public void getQrCode( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new BadCredentialsException( "No current user" );
        }

        String appName = (String) systemSettingManager.getSystemSetting( SettingKey.APPLICATION_TITLE );

        String url = SecurityUtils.generateQrUrl( appName, currentUser );

        Map<String, Object> map = new HashMap<>();
        map.put( "url", url );

        response.setStatus( HttpServletResponse.SC_ACCEPTED );
        response.setContentType( "application/json" );
        jsonMapper.writeValue( response.getOutputStream(), map );
    }

    @RequestMapping( value = "/authenticate", method = RequestMethod.GET, produces = "application/json" )
    public void authenticate2FA( @RequestParam String code, HttpServletRequest request, HttpServletResponse response )
    {
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser == null )
        {
            throw new BadCredentialsException( "No current user" );
        }

        if ( !SecurityUtils.verify( currentUser.getUserCredentials(), code ) )
        {
            webMessageService.send( WebMessageUtils.unathorized( "2FA code not authenticated" ), response, request );
        }
        else
        {
            webMessageService.send( WebMessageUtils.ok( "2FA code authenticated" ), response, request );
        }
    }
}
