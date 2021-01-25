package org.hisp.dhis.webapi.filter;



import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.configuration.ConfigurationService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Slf4j
@Component
public class CorsFilter
    implements Filter, InitializingBean
{
    private static CorsFilter instance;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        instance = this;
    }

    public static CorsFilter get()
    {
        return instance;
    }

    public static final String CORS_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";

    public static final String CORS_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    public static final String CORS_MAX_AGE = "Access-Control-Max-Age";

    public static final String CORS_ALLOW_HEADERS = "Access-Control-Allow-Headers";

    public static final String CORS_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    public static final String CORS_REQUEST_HEADERS = "Access-Control-Request-Headers";

    public static final String CORS_ALLOW_METHODS = "Access-Control-Allow-Methods";

    public static final String CORS_REQUEST_METHOD = "Access-Control-Request-Method";

    public static final String CORS_ORIGIN = "Origin";

    private static final String EXPOSED_HEADERS = "ETag, Location";

    private static final Integer MAX_AGE = 60 * 60; // 1hr max-age

    @Autowired
    private ConfigurationService configurationService;

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain filterChain )
        throws IOException, ServletException
    {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader( CORS_ORIGIN );

        // Origin header is required for CORS requests

        if ( StringUtils.isEmpty( origin ) )
        {
            filterChain.doFilter( request, response );
            return;
        }

        if ( !isOriginWhitelisted( request, origin ) )
        {
            log.debug( "CORS request with origin " + origin + " is not whitelisted" );
            filterChain.doFilter( request, response );
            return;
        }

        response.addHeader( CORS_ALLOW_CREDENTIALS, "true" );
        response.addHeader( CORS_ALLOW_ORIGIN, origin );
        response.addHeader( "Vary", CORS_ORIGIN );

        if ( isPreflight( request ) )
        {
            String requestHeaders = request.getHeader( CORS_REQUEST_HEADERS );
            String requestMethod = request.getHeader( CORS_REQUEST_METHOD );

            response.addHeader( CORS_ALLOW_METHODS, requestMethod );
            response.addHeader( CORS_ALLOW_HEADERS, requestHeaders );
            response.addHeader( CORS_MAX_AGE, String.valueOf( MAX_AGE ) );

            response.setStatus( HttpServletResponse.SC_NO_CONTENT );

            // CORS preflight requires a 2xx status code, so short-circuit the filter chain

            return;
        }
        else
        {
            response.addHeader( CORS_EXPOSE_HEADERS, EXPOSED_HEADERS );
        }

        filterChain.doFilter( request, response );
    }

    private boolean isPreflight( HttpServletRequest request )
    {
        return RequestMethod.OPTIONS.toString().equals( request.getMethod() )
            && !StringUtils.isEmpty( request.getHeader( CORS_ORIGIN ) )
            && !StringUtils.isEmpty( request.getHeader( CORS_REQUEST_METHOD ) );
    }

    private boolean isOriginWhitelisted( HttpServletRequest request, String origin )
    {
        HttpServletRequestEncodingWrapper encodingWrapper = new HttpServletRequestEncodingWrapper( request );
        UriComponentsBuilder uriBuilder = ServletUriComponentsBuilder.fromContextPath( encodingWrapper )
            .replacePath( "" );

        String forwardedProto = request.getHeader( "X-Forwarded-Proto" );

        if ( !StringUtils.isEmpty( forwardedProto ) )
        {
            uriBuilder.scheme( forwardedProto );
        }

        String localUrl = uriBuilder.build().toString();

        return !StringUtils.isEmpty( origin ) && (localUrl.equals( origin ) ||
            configurationService.isCorsWhitelisted( origin ));
    }

    @Override
    public void init( FilterConfig filterConfig )
    {
    }

    @Override
    public void destroy()
    {
    }

    /**
     * Simple HttpServletRequestWrapper implementation that makes sure that the query string is properly encoded.
     */
    class HttpServletRequestEncodingWrapper extends HttpServletRequestWrapper
    {
        public HttpServletRequestEncodingWrapper( HttpServletRequest request )
        {
            super( request );
        }

        @Override
        public String getQueryString()
        {
            String queryString = super.getQueryString();

            if ( !StringUtils.isEmpty( queryString ) )
            {
                try
                {
                    return URLEncoder.encode( queryString, "UTF-8" );
                }
                catch ( UnsupportedEncodingException ignored )
                {
                }
            }

            return queryString;
        }
    }
}
