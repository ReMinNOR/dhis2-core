package org.hisp.dhis.security;





import lombok.extern.slf4j.Slf4j;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;
import org.springframework.security.web.DefaultRedirectStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hisp.dhis.webapi.filter.CustomAuthenticationFilter.PARAM_AUTH_ONLY;

/**
 * @author mortenoh
 */
@Slf4j
public class MappedRedirectStrategy
    extends DefaultRedirectStrategy
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private Map<String, String> redirectMap = new HashMap<>();

    public Map<String, String> getRedirectMap()
    {
        return redirectMap;
    }

    public void setRedirectMap( Map<String, String> redirectMap )
    {
        this.redirectMap = redirectMap;
    }

    private DeviceResolver deviceResolver;

    public void setDeviceResolver( DeviceResolver deviceResolver )
    {
        this.deviceResolver = deviceResolver;
    }

    // -------------------------------------------------------------------------
    // DefaultRedirectStrategy implementation
    // -------------------------------------------------------------------------

    @Override
    public void sendRedirect( HttpServletRequest request, HttpServletResponse response, String url )
        throws IOException
    {
        // ---------------------------------------------------------------------
        // Check if redirect should be skipped - for cookie authentication only
        // ---------------------------------------------------------------------

        String authOnly = (String) request.getAttribute( PARAM_AUTH_ONLY );

        if ( "true".equals( authOnly ) )
        {
            return;
        }

        // ---------------------------------------------------------------------
        // Ignore certain ajax requests
        // ---------------------------------------------------------------------

        for ( String key : redirectMap.keySet() )
        {
            if ( url.contains(key) )
            {
                url = url.replaceFirst( key, redirectMap.get( key ) );
            }
        }

        // ---------------------------------------------------------------------
        // Redirect to mobile start pages
        // ---------------------------------------------------------------------

        Device device = deviceResolver.resolveDevice( request );

        if ( (device.isMobile() || device.isTablet()) )
        {
            url = getRootPath( request ) + "/";
        }

        log.debug( "Redirecting to " + url );

        super.sendRedirect( request, response, url );
    }

    public String getRootPath( HttpServletRequest request )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( request.getScheme() );

        builder.append( "://" ).append( request.getServerName() );

        if ( request.getServerPort() != 80 && request.getServerPort() != 443 )
        {
            builder.append( ":" ).append( request.getServerPort() );
        }

        builder.append( request.getContextPath() );

        return builder.toString();
    }
}
