package org.hisp.dhis.webapi.filter;



import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class CustomAuthenticationFilter implements InitializingBean, Filter
{
    private static CustomAuthenticationFilter instance;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        instance = this;
    }

    public static CustomAuthenticationFilter get()
    {
        return instance;
    }

    public static final String PARAM_MOBILE_VERSION = "mobileVersion";

    public static final String PARAM_AUTH_ONLY = "authOnly";

    @Override
    public void init( FilterConfig filterConfig )
        throws ServletException
    {
    }

    @Override
    public void doFilter( ServletRequest request, ServletResponse response, FilterChain filterChain )
        throws IOException, ServletException
    {
        String mobileVersion = request.getParameter( PARAM_MOBILE_VERSION );
        String authOnly = request.getParameter( PARAM_AUTH_ONLY );

        if ( mobileVersion != null )
        {
            request.setAttribute( PARAM_MOBILE_VERSION, mobileVersion );
        }

        if ( authOnly != null )
        {
            request.setAttribute( PARAM_AUTH_ONLY, authOnly );
        }

        filterChain.doFilter( request, response );
    }

    @Override
    public void destroy()
    {
    }
}

