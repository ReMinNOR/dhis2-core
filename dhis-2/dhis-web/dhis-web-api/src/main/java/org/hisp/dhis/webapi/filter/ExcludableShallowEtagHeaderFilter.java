package org.hisp.dhis.webapi.filter;



import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

/**
 * <p>Subclass of {@link org.springframework.web.filter.ShallowEtagHeaderFilter} 
 * which allows exclusion of URIs matching a regex.
 *
 * <p>The regex is given as the init-param named 'excludeUriRegex' in the filter 
 * configuration.
 *
 * <p>Example configuration:
 * 
 * <pre>
 * {@code
 * <filter>
 *     <filter-name>ShallowEtagHeaderFilter</filter-name>
 *     <filter-class>org.hisp.dhis.webapi.filter.ExcludableShallowEtagHeaderFilter</filter-class>
 *     <init-param>
 *         <param-name>excludeUriRegex</param-name>
 *         <param-value>/api/dataValues|/api/dataValues/files</param-value>
 *     </init-param>
 * </filter>
 * }
 * </pre>
 * 
 * <p>The example exactly matches and excludes any request to the '/api/dataValues' 
 * and '/api/dataValues/files' from the filter.
 * 
 * @author Lars Helge Overland
 * @author Halvdan Hoem Grelland
 */
@Slf4j
@WebFilter( urlPatterns = {
    "/api/*"
},
    initParams = {
        @WebInitParam( name = "excludeUriRegex", value = "/api/(\\d{2}/)?dataValueSets|/api/(\\d{2}/)?dataValues|/api/(\\d{2}/)?fileResources" )
    } )
public class ExcludableShallowEtagHeaderFilter
    extends ShallowEtagHeaderFilter
{

    private static final String EXCLUDE_URI_REGEX_VAR_NAME = "excludeUriRegex";

    private Pattern pattern = null;

    @Override
    protected void initFilterBean()
        throws ServletException
    {
        FilterConfig filterConfig = getFilterConfig();

        String excludeRegex = filterConfig != null ? filterConfig.getInitParameter( EXCLUDE_URI_REGEX_VAR_NAME ) : null;

        Assert.notNull( excludeRegex, String.format( excludeRegex, 
            "Parameter '%s' must be specified for ExcludableShallowEtagHeaderFilter", EXCLUDE_URI_REGEX_VAR_NAME ) );
        
        pattern = Pattern.compile( excludeRegex );
        
        log.debug( String.format( "ExcludableShallowEtagHeaderFilter initialized with %s: '%s'", EXCLUDE_URI_REGEX_VAR_NAME, excludeRegex ) );
    }

    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain )
        throws ServletException, IOException
    {
        String uri = request.getRequestURI();

        boolean match = pattern.matcher( uri ).find();
                
        if ( match )
        {
            filterChain.doFilter( request, response ); // Proceed without invoking this filter
        }
        else
        {
            super.doFilterInternal( request, response, filterChain ); // Invoke this filter
        }
    }
}
