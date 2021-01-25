package org.hisp.dhis.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.hibernate.exception.CreateAccessDeniedException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.ReadAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * This interceptor will intercept exceptions and redirect to appropriate
 * exception results / pages defined in the global-results section in the XWork
 * configuration.
 *
 * @author Torgeir Lorange Ostby
 * @version $Id: WebWorkExceptionInterceptor.java 6335 2008-11-20 11:11:26Z larshelg $
 */
@Slf4j
public class ExceptionInterceptor
    implements Interceptor
{
    public static final String EXCEPTION_RESULT_KEY = "onExceptionReturn";
    public static final String EXCEPTION_RESULT_DEFAULT = "exceptionDefault";
    public static final String EXCEPTION_RESULT_PLAIN_TEXT = "plainTextError";
    public static final String EXCEPTION_RESULT_PAGE_ACCESS_DENIED = "pageAccessDenied";
    public static final String EXCEPTION_RESULT_PAGE_JSON_ACCESS_DENIED = "jsonAccessDenied";

    public static final String EXCEPTION_RESULT_CREATE_ACCESS_DENIED = "createAccessDenied";
    public static final String EXCEPTION_RESULT_READ_ACCESS_DENIED = "readAccessDenied";
    public static final String EXCEPTION_RESULT_UPDATE_ACCESS_DENIED = "updateAccessDenied";
    public static final String EXCEPTION_RESULT_DELETE_ACCESS_DENIED = "deleteAccessDenied";

    public static final String TEMPLATE_KEY_EXCEPTION = "exception";
    public static final String TEMPLATE_KEY_SHOW_STACK_TRACE = "showStackTrace";

    // -------------------------------------------------------------------------
    // Show stack trace parameter. Defaults to true
    // -------------------------------------------------------------------------

    private boolean showStackTrace = true;

    public void setShowStackTrace( boolean showStackTrace )
    {
        this.showStackTrace = showStackTrace;
    }

    private List<String> ignoredExceptions = new ArrayList<>();

    public void setIgnoredExceptions( List<String> ignoredExceptions )
    {
        this.ignoredExceptions = ignoredExceptions;
    }

    // -------------------------------------------------------------------------
    // Interface implementation
    // -------------------------------------------------------------------------

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public String intercept( ActionInvocation actionInvocation )
    {
        try
        {
            return actionInvocation.invoke();
        }
        catch ( Exception e )
        {
            // -----------------------------------------------------------------
            // Save exception to value stack
            // -----------------------------------------------------------------

            Map<String, Object> parameterMap = new HashMap<>( 3 );
            parameterMap.put( TEMPLATE_KEY_EXCEPTION, e );
            parameterMap.put( TEMPLATE_KEY_SHOW_STACK_TRACE, showStackTrace );
            actionInvocation.getStack().push( parameterMap );

            // -----------------------------------------------------------------
            // Find and return result name
            // -----------------------------------------------------------------

            Map<?, ?> params = actionInvocation.getProxy().getConfig().getParams();
            String exceptionResultName = (String) params.get( EXCEPTION_RESULT_KEY );

            if ( e instanceof CreateAccessDeniedException )
            {
                return EXCEPTION_RESULT_CREATE_ACCESS_DENIED;
            }

            if ( e instanceof ReadAccessDeniedException )
            {
                return EXCEPTION_RESULT_READ_ACCESS_DENIED;
            }

            if ( e instanceof UpdateAccessDeniedException )
            {
                return EXCEPTION_RESULT_UPDATE_ACCESS_DENIED;
            }

            if ( e instanceof DeleteAccessDeniedException )
            {
                return EXCEPTION_RESULT_DELETE_ACCESS_DENIED;
            }

            if ( e instanceof AccessDeniedException || e instanceof InsufficientAuthenticationException )
            {
                if ( EXCEPTION_RESULT_PLAIN_TEXT.equals( exceptionResultName ) )
                {
                    return EXCEPTION_RESULT_PAGE_JSON_ACCESS_DENIED; // Access denied as JSON
                }

                return EXCEPTION_RESULT_PAGE_ACCESS_DENIED; // Access denied as nice page
            }

            // -----------------------------------------------------------------
            // Check if exception should be ignored
            // -----------------------------------------------------------------

            Throwable t = e;

            boolean ignore = false;

            checkIgnore:
            do
            {
                if ( ignoredExceptions.contains( t.getClass().getName() ) )
                {
                    ignore = true;
                    break checkIgnore;
                }
            }
            while ( (t = t.getCause()) != null );

            // -----------------------------------------------------------------
            // Log exception
            // -----------------------------------------------------------------

            if ( !ignore )
            {
                log.error( "Error while executing action", e );
            }
            else
            {
                log.info( "Ignored exception: " + e.getClass().getName() );
            }

            exceptionResultName = defaultIfEmpty( exceptionResultName, EXCEPTION_RESULT_DEFAULT );

            return ignore ? EXCEPTION_RESULT_PLAIN_TEXT : exceptionResultName;
        }
    }
}
