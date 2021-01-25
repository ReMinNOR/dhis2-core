package org.hisp.dhis.useraccount.action;



import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class IsAccountRecoveryAllowedAction
    implements Action
{
    @Autowired
    private SystemSettingManager systemSettingManager;
    
    @Override
    public String execute()
    {
        boolean enabled = systemSettingManager.accountRecoveryEnabled();
        
        return enabled ? SUCCESS : ERROR;
    }
}
