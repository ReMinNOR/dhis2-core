package org.hisp.dhis.security.intercept;



import java.util.ArrayList;
import java.util.List;


import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * Interceptor that will run a list of actions when the user first logins.
 * 
 * @author mortenoh
 */
@Slf4j
public class LoginInterceptor
    implements Interceptor
{
    private static final long serialVersionUID = -5376334780350610573L;

    public static final String JLI_SESSION_VARIABLE = "JLI";

    private List<Action> actions = new ArrayList<>();

    /**
     * @param actions List of actions to run on login.
     */
    public void setActions( List<Action> actions )
    {
        this.actions = actions;
    }

    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Boolean jli = (Boolean) ServletActionContext.getRequest().getSession()
            .getAttribute( LoginInterceptor.JLI_SESSION_VARIABLE );

        if ( jli != null )
        {
            log.debug( "JLI marker is present. Running " + actions.size() + " JLI actions." );

            for ( Action a : actions )
            {
                a.execute();
            }

            ServletActionContext.getRequest().getSession().removeAttribute( LoginInterceptor.JLI_SESSION_VARIABLE );
        }

        return invocation.invoke();
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }
}
