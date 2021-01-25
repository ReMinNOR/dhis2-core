package org.hisp.dhis.security.filter;



import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.user.CurrentUserService;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: RequiredLoginFilter.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
public class RequiredLoginFilter
    implements Filter
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private String loginPageUrl;

    public void setLoginPageUrl( String loginFormPath )
    {
        this.loginPageUrl = loginFormPath;
    }

    // -------------------------------------------------------------------------
    // Filter implementation
    // -------------------------------------------------------------------------

    @Override
    public void init( FilterConfig filterConfig )
        throws ServletException
    {
    }

    @Override
    public void doFilter( ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain )
        throws IOException, ServletException
    {
        // ---------------------------------------------------------------------
        // Convert to HttpServletRequest and -Response
        // ---------------------------------------------------------------------

        if ( !(servletRequest instanceof HttpServletRequest) )
        {
            throw new ServletException( "Can only handle HttpServletRequests. Got: "
                + servletRequest.getClass().getName() );
        }

        if ( !(servletResponse instanceof HttpServletResponse) )
        {
            throw new ServletException( "Can only handle HttpServletResponses. Got: "
                + servletResponse.getClass().getName() );
        }

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // ---------------------------------------------------------------------
        // Redirect to login page if the request URL is not the login page URL
        // and the user isn't logged in
        // ---------------------------------------------------------------------

        String currentUsername = currentUserService.getCurrentUsername();

        if ( !request.getServletPath().equals( loginPageUrl ) && currentUsername == null )
        {
            StringBuffer targetUrl = request.getRequestURL();

            if ( request.getQueryString() != null )
            {
                targetUrl.append( '?' );
                targetUrl.append( request.getQueryString() );
            }

            response.sendRedirect( response.encodeRedirectURL( request.getContextPath() + loginPageUrl ) );

            return;
        }

        // ---------------------------------------------------------------------
        // Continue the filter chain
        // ---------------------------------------------------------------------

        filterChain.doFilter( request, response );
    }

    @Override
    public void destroy()
    {
    }
}
