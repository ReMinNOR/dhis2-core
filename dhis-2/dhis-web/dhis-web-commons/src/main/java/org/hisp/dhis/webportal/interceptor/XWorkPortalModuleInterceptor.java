package org.hisp.dhis.webportal.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webportal.module.ModuleManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class XWorkPortalModuleInterceptor
    implements Interceptor
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 8996189907119658364L;

    private static final String KEY_MENU_MODULES = "menuModules";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModuleManager moduleManager;

    public void setModuleManager( ModuleManager moduleManager )
    {
        this.moduleManager = moduleManager;
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
    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {
        String contextPath = ContextUtils.getContextPath( ServletActionContext.getRequest() );
        
        Map<String, Object> handle = new HashMap<>( 2 );

        handle.put( KEY_MENU_MODULES, moduleManager.getAccessibleMenuModulesAndApps( contextPath ) );

        actionInvocation.getStack().push( handle );

        return actionInvocation.invoke();
    }
}
