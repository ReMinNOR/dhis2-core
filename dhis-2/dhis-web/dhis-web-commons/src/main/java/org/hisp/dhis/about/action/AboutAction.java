package org.hisp.dhis.about.action;



import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.system.SystemInfo;
import org.hisp.dhis.system.SystemService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.util.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Dang Duy Hieu
 */
public class AboutAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SystemService systemService;

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private SystemInfo info;

    public SystemInfo getInfo()
    {
        return info;
    }

    private String userAgent;

    public String getUserAgent()
    {
        return userAgent;
    }

    private boolean currentUserIsSuper;

    public boolean getCurrentUserIsSuper()
    {
        return currentUserIsSuper;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        info = systemService.getSystemInfo();

        HttpServletRequest request = ServletActionContext.getRequest();

        userAgent = request.getHeader( ContextUtils.HEADER_USER_AGENT );

        currentUserIsSuper = currentUserService.currentUserIsSuper();

        return SUCCESS;
    }
}
