package org.hisp.dhis.useraccount.action;



import org.hisp.dhis.configuration.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class IsSelfRegistrationAllowedAction
    implements Action
{
    @Autowired
    private ConfigurationService configurationService;
    
    @Override
    public String execute()
        throws Exception
    {
        boolean allowed = configurationService.getConfiguration().selfRegistrationAllowed();
        
        return allowed ? SUCCESS : ERROR;
    }
}
