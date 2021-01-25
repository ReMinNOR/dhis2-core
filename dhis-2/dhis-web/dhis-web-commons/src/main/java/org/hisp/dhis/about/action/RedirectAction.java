package org.hisp.dhis.about.action;



import com.opensymphony.xwork2.Action;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.appmanager.App;
import org.hisp.dhis.appmanager.AppManager;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class RedirectAction
    implements Action
{
    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private AppManager appManager;

    private String redirectUrl;

    public String getRedirectUrl()
    {
        return redirectUrl;
    }

    @Override
    public String execute()
        throws Exception
    {
        String startModule = (String) systemSettingManager.getSystemSetting( SettingKey.START_MODULE );

        String contextPath = (String) ContextUtils.getContextPath( ServletActionContext.getRequest() );

        if ( startModule != null && !startModule.trim().isEmpty() )
        {
            if ( startModule.startsWith( "app:" ) )
            {
                List<App> apps = appManager.getApps( contextPath );

                for ( App app : apps )
                {
                    if ( app.getShortName().equals( startModule.substring( "app:".length() ) ) )
                    {
                        redirectUrl = app.getLaunchUrl();
                        return SUCCESS;
                    }
                }
            }
            else
            {
                redirectUrl = "../" + startModule + "/";
                return SUCCESS;
            }
        }

        redirectUrl = "../dhis-web-dashboard/";
        return SUCCESS;
    }
}
