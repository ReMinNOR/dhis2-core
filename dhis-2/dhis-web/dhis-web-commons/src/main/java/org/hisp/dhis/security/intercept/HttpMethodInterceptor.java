package org.hisp.dhis.security.intercept;





import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author Lars Helge Overland
 */
@Slf4j
public class HttpMethodInterceptor
    implements Interceptor
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -7882464040475459114L;

    private static final String DEFAULT_METHOD = "POST";
    
    protected String allowedMethod = DEFAULT_METHOD;
    
    public void setAllowedMethod( String allowedMethod )
    {
        this.allowedMethod = allowedMethod;
    }

    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        String method = ServletActionContext.getRequest().getMethod();
        
        log.info( "Method: " + method );
        
        if ( method == null || !method.trim().toLowerCase().equals( allowedMethod.trim().toLowerCase() ) )
        {
            log.warn( "HTTP method ' " + allowedMethod + "' only allowed for this request" );
            
            return null;
        }
        
        return invocation.invoke();
    }

    @Override
    public void init()
    {
    }

    @Override
    public void destroy()
    {        
    }
}
