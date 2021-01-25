package org.hisp.dhis.webportal.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.hisp.dhis.user.CurrentUserService;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class XWorkPortalUserInterceptor
    implements Interceptor
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 2809606672626282043L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    // -------------------------------------------------------------------------
    // Interceptor implementation
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
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Map<String, Object> map = new HashMap<>( 3 );

        map.put( "currentUsername", currentUserService.getCurrentUsername() );
        map.put( "currentUser", currentUserService.getCurrentUser() );

        invocation.getStack().push( map );

        return invocation.invoke();
    }
}
