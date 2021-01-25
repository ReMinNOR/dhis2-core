package org.hisp.dhis.servlet.filter;



import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.appmanager.AppStatus;
import org.hisp.dhis.commons.util.StreamUtils;
import org.hisp.dhis.util.DateUtils;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Austin McGee <austin@dhis2.org>
 */
 @Slf4j
 @Component
public class AppOverrideFilter
    extends OncePerRequestFilter
{
    @Autowired
    private AppManager appManager;

    @Autowired
    private ObjectMapper jsonMapper;

    public static final String APP_PATH_PATTERN = "^/" + AppManager.BUNDLED_APP_PREFIX + "(" + String.join("|", AppManager.BUNDLED_APPS) + ")/(.*)";
    // public static final String REDIRECT_APP_PATH_PATTERN = "^/" + AppController.RESOURCE_PATH + "-(" + String.join("|", AppManager.BUNDLED_APPS) + ")/(.*)"

    // -------------------------------------------------------------------------
    // Filter implementation
    // -------------------------------------------------------------------------

    // From AppController.java (some duplication)
    private void serveInstalledAppResource( App app, String resourcePath, HttpServletRequest request, HttpServletResponse response )
        throws IOException
    {
        // Get page requested

        log.debug( String.format( "Serving app resource: '%s'", resourcePath ) );

        // Handling of 'manifest.webapp'
        if ( "manifest.webapp".equals( resourcePath ) )
        {
            // If request was for manifest.webapp, check for * and replace with host
            if ( app.getActivities() != null && app.getActivities().getDhis() != null && "*".equals( app.getActivities().getDhis().getHref() ) )
            {
                String contextPath = ContextUtils.getContextPath( request );
                log.debug( String.format( "Manifest context path: '%s'", contextPath ) );
                app.getActivities().getDhis().setHref( contextPath );
            }

            jsonMapper.writeValue( response.getOutputStream(), app );
        }
        else if ( "index.action".equals( resourcePath ) )
        {
            response.sendRedirect( app.getLaunchUrl() );
        }
        // Any other resource
        else
        {
            // Retrieve file
            Resource resource = appManager.getAppResource( app, resourcePath );

            if ( resource == null )
            {
                response.sendError( HttpServletResponse.SC_NOT_FOUND );
                return;
            }

            String filename = resource.getFilename();
            log.debug( String.format( "App filename: '%s'", filename ) );

            if ( new ServletWebRequest( request, response ).checkNotModified( resource.lastModified() ) )
            {
                response.setStatus( HttpServletResponse.SC_NOT_MODIFIED );
                return;
            }

            String mimeType = request.getSession().getServletContext().getMimeType( filename );

            if ( mimeType != null )
            {
                response.setContentType( mimeType );
            }

            response.setContentLength( (int) resource.contentLength() );
            response.setHeader( "Last-Modified", DateUtils.getHttpDateString( new Date( resource.lastModified() ) ) );

            StreamUtils.copyThenCloseInputStream( resource.getInputStream(), response.getOutputStream() );
        }
    }

    @Override
    protected void doFilterInternal( HttpServletRequest req, HttpServletResponse res, FilterChain chain )
        throws IOException, ServletException
    {
        String requestURI = req.getRequestURI();

        Pattern p = Pattern.compile( APP_PATH_PATTERN );
        Matcher m = p.matcher( requestURI );

        if ( m.find() )
        {
            String appName = m.group( 1 );
            String resourcePath = m.group( 2 );

            log.debug( "AppOverrideFilter :: Matched for URI " + requestURI );

            App app = appManager.getApp( appName );

            if ( app != null && app.getAppState() != AppStatus.DELETION_IN_PROGRESS )
            {
                log.debug( "AppOverrideFilter :: Overridden app " + appName + " found, serving override" );
                serveInstalledAppResource( app, resourcePath, req, res );

                return;
            }
            else
            {
                log.debug( "AppOverrideFilter :: App " + appName + " not found, falling back to bundled app" );
            }
        }

        chain.doFilter( req, res );
    }
}
