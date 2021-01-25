package org.hisp.dhis.webportal.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import org.hisp.dhis.webportal.menu.MenuState;
import org.hisp.dhis.webportal.menu.MenuStateManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class XWorkPortalMenuInterceptor
    implements Interceptor
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6165469913988612348L;

    private static final String KEY_MENU_STATE = "menuState";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MenuStateManager menuStateManager;

    public void setMenuStateManager( MenuStateManager menuStateManager )
    {
        this.menuStateManager = menuStateManager;
    }

    // -------------------------------------------------------------------------
    // AroundInterceptor implementation
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
        Map<String, MenuState> menuStateMap = new HashMap<>( 1 );

        MenuState menuState = menuStateManager.getMenuState();

        if ( menuState == null )
        {
            menuState = MenuState.VISIBLE;
        }

        menuStateMap.put( KEY_MENU_STATE, menuState );

        invocation.getStack().push( menuStateMap );

        return invocation.invoke();
    }
}
