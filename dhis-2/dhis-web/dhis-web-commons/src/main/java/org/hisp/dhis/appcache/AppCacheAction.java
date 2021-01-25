package org.hisp.dhis.appcache;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class AppCacheAction implements Action
{
    private CurrentUserService currentUserService;

    @Autowired
    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private SystemService systemService;

    @Autowired
    public void setSystemService( SystemService systemService )
    {
        this.systemService = systemService;
    }

    private String username;

    public String getUsername()
    {
        return username;
    }

    private SystemInfo systemInfo;

    public SystemInfo getSystemInfo()
    {
        return systemInfo;
    }

    @Override
    public String execute() throws Exception
    {
        username = currentUserService.getCurrentUsername();

        systemInfo = systemService.getSystemInfo();

        return SUCCESS;
    }
}
