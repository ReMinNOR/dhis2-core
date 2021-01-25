package org.hisp.dhis.interceptor;



import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpMethod;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Lars Helge Overland
 */
public class CacheInterceptor
    implements Interceptor
{
    private int seconds = 604800; // One week
    
    public void setSeconds( int seconds )
    {
        this.seconds = seconds;
    }

    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();
        
        if ( HttpMethod.GET == HttpMethod.resolve( request.getMethod() ) )
        {
            response.setHeader( "Cache-Control", CacheControl.maxAge( seconds, TimeUnit.SECONDS ).cachePublic().getHeaderValue() );
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
