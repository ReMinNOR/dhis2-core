package org.hisp.dhis.util;



import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;


import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
@Slf4j
public abstract class StreamActionSupport
    implements Action
{
    // -------------------------------------------------------------------------
    // ActionSupport implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {        
        HttpServletResponse response = ServletActionContext.getResponse();
        
        String contentType = getContentType();
        boolean disallowCache = disallowCache();
        String filename = getFilename();
        boolean attachment = attachment();
        
        ContextUtils.configureResponse( response, contentType, disallowCache, filename, attachment );
        
        log.debug( "Content type: " + contentType + ", disallow cache: " + 
            disallowCache + ", filename: " + filename + ", attachment: " + attachment );
        
        try ( OutputStream out = response.getOutputStream() )
        {            
            return execute( response, out );
        }
    }

    // -------------------------------------------------------------------------
    // Abstract methods
    // -------------------------------------------------------------------------

    protected abstract String execute( HttpServletResponse response, OutputStream out )
        throws Exception;
    
    protected abstract String getContentType();
    
    protected abstract String getFilename();
    
    protected boolean disallowCache()
    {
        return true;
    }
    
    protected boolean attachment()
    {
        return true;
    }
}
