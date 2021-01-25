package org.hisp.dhis.useraccount.action;



import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class GetCurrentUserAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private UserCredentials userCredentials;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    public UserCredentials getUserCredentials()
    {
        return userCredentials;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        User user = currentUserService.getCurrentUser();

        userCredentials = user.getUserCredentials();

        return SUCCESS;
    }
}
