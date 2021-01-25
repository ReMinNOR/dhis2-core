package org.hisp.dhis.webportal.module;



import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: CurrentModuleDetectorFilter.java 6216 2008-11-06 18:06:42Z eivindwa $
 */
@Slf4j
public class CurrentModuleDetectorFilter
    implements Filter
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModuleManager moduleManager;

    public void setModuleManager( ModuleManager moduleManager )
    {
        this.moduleManager = moduleManager;
    }

    // -------------------------------------------------------------------------
    // Filter
    // -------------------------------------------------------------------------

    @Override
    public void init( FilterConfig filterConfig )
    {
    }

    @Override
    public void destroy()
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

        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // ---------------------------------------------------------------------
        // Detect requested module
        // ---------------------------------------------------------------------

        String actionURL = request.getServletPath();

        int endOfNamespace = actionURL.lastIndexOf( '/' );

        String namespace = actionURL.substring( 0, endOfNamespace );

        Module module = moduleManager.getModuleByNamespace( namespace );

        if ( module == null )
        {
            log.error( "Requesting a module which doesn't exist: '" + namespace + "' (" + actionURL + ")" );
        }
        else
        {
            moduleManager.setCurrentModule( module );
        }

        // ---------------------------------------------------------------------
        // Continue with next filter
        // ---------------------------------------------------------------------

        filterChain.doFilter( servletRequest, servletResponse );
    }
}
