package org.hisp.dhis.servlet.filter;



import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletResponse;




/**
 * @author Torgeir Lorange Ostby
 * @version $Id: HttpRedirectFilter.java 2869 2007-02-20 14:26:09Z andegje $
 */
@Slf4j
@WebFilter( urlPatterns = {
    "/"
},
    initParams = {
        @WebInitParam( name = "redirectPath", value = "dhis-web-commons-about/redirect.action" ),
        @WebInitParam( name = "urlPattern", value = "index\\.html|/$" )
    } )
public class HttpRedirectFilter
    implements Filter
{
    private static final String REDIRECT_PATH_KEY = "redirectPath";

    private String redirectPath;

    // -------------------------------------------------------------------------
    // Filter implementation
    // -------------------------------------------------------------------------

    @Override
    public void init( FilterConfig config )
    {
        redirectPath = config.getInitParameter( REDIRECT_PATH_KEY );
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException {
        log.debug( "Redirecting to: " + redirectPath );
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ( redirectPath == null )
        {
            String msg = "HttpRedirectFilter was not properly initialised. \"" + REDIRECT_PATH_KEY + "\" must be specified.";
            
            httpResponse.setContentType( "text/plain" );
            httpResponse.getWriter().print( msg );

            log.warn( msg );
            
            return;
        }

        httpResponse.sendRedirect( redirectPath );

    }

    @Override
    public void destroy()
    {
    }
}
