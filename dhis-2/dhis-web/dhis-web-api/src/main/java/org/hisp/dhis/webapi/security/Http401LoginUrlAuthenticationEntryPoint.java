package org.hisp.dhis.webapi.security;



import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.render.RenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class Http401LoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint
{
    @Autowired
    private RenderService renderService;

    public Http401LoginUrlAuthenticationEntryPoint( String loginFormUrl )
    {
        super( loginFormUrl );
    }

    @Override
    public void commence( HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException )
        throws IOException, ServletException
    {
        if ( "XMLHttpRequest".equals( request.getHeader( "X-Requested-With" ) ) )
        {
            response.setStatus( HttpServletResponse.SC_UNAUTHORIZED );
            response.setContentType( MediaType.APPLICATION_JSON_VALUE );
            renderService.toJson( response.getOutputStream(), WebMessageUtils.unathorized( "Unauthorized" ) );
            return;
        }

        super.commence( request, response, authException );
    }
}
