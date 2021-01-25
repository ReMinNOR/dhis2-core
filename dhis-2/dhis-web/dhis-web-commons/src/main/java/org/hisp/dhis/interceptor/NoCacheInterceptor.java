package org.hisp.dhis.interceptor;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.http.HttpMethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * Interceptor which sets HTTP headers which instructs clients not to
 * cache the response. This is the default behavior for Struts-generated
 * responses. Does not set the cache interceptor if already set, allowing
 * other interceptors to set cache headers for special cases.
 * 
 * @author Lars Helge Overland
 */
public class NoCacheInterceptor
    implements Interceptor
{    
    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        
        String header = response.getHeader( ContextUtils.HEADER_CACHE_CONTROL );
        boolean headerSet = header != null && !header.trim().isEmpty();
        
        if ( !headerSet && HttpMethod.GET == HttpMethod.resolve( request.getMethod() ) )
        {
            ContextUtils.setNoStore( response );
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
