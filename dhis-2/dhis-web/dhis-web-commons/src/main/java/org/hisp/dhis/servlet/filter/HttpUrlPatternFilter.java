package org.hisp.dhis.servlet.filter;



import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>Generic abstract HTTP filter class which allows for matching
 * request URLs using regular expressions. This filter class could be 
 * sub-classed by concrete HTTP filters.
 * 
 * <p>The filter requires an <code>init-param</code> with a 
 * <code>param-name</code> called <code>urlPattern</code> and a regular 
 * expression as the <code>param-value</code>.
 * 
 * <p>The filter will compile the <code>urlPattern</code> parameter
 * value and match it against the HTTP request URI using the 
 * <code>find</code> function of the {@link Matcher}. This implies 
 * that the regular expression only need to match a subsequence of the 
 * request URI, not the entire URI.
 * 
 * <p>Example configuration:
 * 
 * <pre>
 * {@code
 * <filter>
 *     <filter-name>yourRegexFilter</filter-name>
 *     <filter-class>org.hisp.dhis.servlet.filter.YourRegexFilter</filter-class>
 *     <init-param>
 *         <param-name>urlPattern</param-name>
 *         <param-value>index\.action|index\.html</param-value>
 *     </init-param>
 * </filter>
 * }
 * </pre>
 * 
 * @author Lars Helge Overland
 */
public abstract class HttpUrlPatternFilter
    implements Filter
{
    private Pattern pattern = null;
    
    private static final String PARAM_NAME = "urlPattern";
    
    @Override
    public final void init( FilterConfig config )
        throws ServletException
    {
        String urlPattern = config.getInitParameter( PARAM_NAME );
        
        if ( StringUtils.isBlank( urlPattern ) )
        {
            throw new IllegalArgumentException( "Init param '" + PARAM_NAME + "' must be specified" );
        }
        
        pattern = Pattern.compile( urlPattern );
        
        doInit( config );
    }

    @Override
    public void destroy()
    {
    }
    
    @Override
    public final void doFilter( ServletRequest request, ServletResponse response, FilterChain chain )
        throws IOException, ServletException
    {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String uri = httpRequest.getRequestURI();
                
        if ( pattern != null && pattern.matcher( uri ).find() )
        {
            doHttpFilter( httpRequest, httpResponse, chain );
        }
        else
        {
            chain.doFilter( request, response ); // Ignore this filter and proceed
        }
    }
    
    /**
     * Hook for performing initialization work on the filter.
     * 
     * @param config the filter configuration.
     */
    public void doInit( FilterConfig config )
    {
    }
        
    /**
     * Perform work on the HTTP request / response chain assuming
     * that the pattern matched the request URL. The
     * <code>FilterChain.doFilter</code> method must be invoked to 
     * pass on the request and response to the next entity in the 
     * chain.
     * 
     * @param request the HTTP request.
     * @param response the HTTP response.
     * @param chain the the filter chain.
     */
    public abstract void doHttpFilter( HttpServletRequest request, HttpServletResponse response, FilterChain chain )
        throws IOException, ServletException;
}
