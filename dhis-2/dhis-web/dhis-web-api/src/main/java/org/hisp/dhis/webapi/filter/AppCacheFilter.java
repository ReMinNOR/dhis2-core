package org.hisp.dhis.webapi.filter;



import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.i18n.ui.locale.UserSettingLocaleManager;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserSettingKey;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Slf4j
@WebFilter( urlPatterns = {
    "*.appcache"
})
public class AppCacheFilter implements Filter
{
    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private SystemService systemService;

    @Autowired
    private UserSettingLocaleManager localeManager;

    @Autowired
    private UserSettingService userSettingService;

    @Override
    public void doFilter( ServletRequest req, ServletResponse res, FilterChain chain )
        throws IOException, ServletException
    {
        if ( req instanceof HttpServletRequest && res instanceof HttpServletResponse )
        {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;

            PrintWriter writer = response.getWriter();
            CharResponseWrapper responseWrapper = new CharResponseWrapper( response );

            chain.doFilter( request, responseWrapper );
            responseWrapper.setContentType( "text/cache-manifest" );

            SystemInfo systemInfo = systemService.getSystemInfo();

            writer.print( responseWrapper.toString() );
            writer.println( "# DHIS2 " + systemInfo.getVersion() + " r" + systemInfo.getRevision() );
            writer.println( "# User: " + currentUserService.getCurrentUsername() );
            writer.println( "# User UI Language: " + localeManager.getCurrentLocale() );
            writer.println( "# User DB Language: " + userSettingService.getUserSetting( UserSettingKey.DB_LOCALE ) );
            writer.println( "# Calendar: " + systemInfo.getCalendar() );
        }
    }

    @Override
    public void init( FilterConfig filterConfig )
        throws ServletException
    {
        log.debug( "Init AppCacheFilter called!" );
    }

    @Override
    public void destroy()
    {
        log.debug( "Destroy AppCacheFilter called!" );
    }
}

class CharResponseWrapper extends HttpServletResponseWrapper
{
    private CharArrayWriter output;

    @Override
    public String toString()
    {
        return output.toString();
    }

    public CharResponseWrapper( HttpServletResponse response )
    {
        super( response );
        output = new CharArrayWriter();
    }

    @Override
    public PrintWriter getWriter()
    {
        return new PrintWriter( output );
    }
}
