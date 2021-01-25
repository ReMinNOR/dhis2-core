package org.hisp.dhis.interceptor;



import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.setting.StyleManager;
import org.hisp.dhis.user.UserSettingKey;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author mortenoh
 */
public class UserSettingInterceptor
    implements Interceptor
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }

    // -------------------------------------------------------------------------
    // UserSettingInterceptor implementation
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
        Map<String, Object> map = new HashMap<>();

        map.put( UserSettingKey.STYLE.getName(), styleManager.getCurrentStyle() );

        invocation.getStack().push( map );

        return invocation.invoke();
    }
}
