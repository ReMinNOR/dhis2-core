package org.hisp.dhis.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import com.opensymphony.xwork2.interceptor.PreResultListener;
import lombok.extern.slf4j.Slf4j;

/**
 * The intention of this class is to stop execution of the pre result listener
 * when an exception is thrown in the action invocation.
 *
 * @author Torgeir Lorange Ostby
 */
@Slf4j
public abstract class AbstractPreResultListener
    implements Interceptor, PreResultListener
{
    private boolean executePreResultListener;

    // -------------------------------------------------------------------------
    // Interceptor implementation
    // -------------------------------------------------------------------------

    @Override
    public final void destroy()
    {
    }

    @Override
    public final void init()
    {
    }

    @Override
    public final String intercept( ActionInvocation actionInvocation ) throws Exception
    {
        actionInvocation.addPreResultListener( this );

        executePreResultListener = true;

        try
        {
            return actionInvocation.invoke();
        }
        catch ( Exception e )
        {
            executePreResultListener = false;
            throw e;
        }
    }

    // -------------------------------------------------------------------------
    // PreResultListener implementation
    // -------------------------------------------------------------------------

    @Override
    public final void beforeResult( ActionInvocation actionInvocation, String result )
    {
        if ( executePreResultListener )
        {
            try
            {
                executeBeforeResult( actionInvocation, result );
            }
            catch ( Exception e )
            {
                log.error( "Error while executing PreResultListener", e );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Abstract method to be implemented by subclasses
    // -------------------------------------------------------------------------

    public abstract void executeBeforeResult( ActionInvocation actionInvocation, String result ) throws Exception;
}
