package org.hisp.dhis.useraccount.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.security.RestoreOptions;
import org.hisp.dhis.security.RestoreType;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jim Grace
 */
public class IsInviteTokenValidAction
    implements Action
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String token;

    public String getToken()
    {
        return token;
    }

    public void setToken( String token )
    {
        this.token = token;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getAccountAction()
    {
        return "invited";
    }

    private String usernameChoice;

    public String getUsernameChoice()
    {
        return usernameChoice;
    }

    private String email;

    public String getEmail()
    {
        return email;
    }

    private String username;

    public String getUsername()
    {
        return username;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        String[] idAndRestoreToken = securityService.decodeEncodedTokens( token );
        String idToken = idAndRestoreToken[0];
        String restoreToken = idAndRestoreToken[1];

        UserCredentials userCredentials = userService.getUserCredentialsByIdToken( idToken );

        if ( userCredentials == null )
        {
            return ERROR;
        }

        String errorMessage = securityService.verifyRestoreToken( userCredentials, restoreToken, RestoreType.INVITE );

        if ( errorMessage != null )
        {
            return ERROR;
        }

        email = userCredentials.getUserInfo().getEmail();
        username = userCredentials.getUsername();

        RestoreOptions restoreOptions = securityService.getRestoreOptions( restoreToken );

        if ( restoreOptions != null )
        {
            usernameChoice = Boolean.toString( restoreOptions.isUsernameChoice() );
        }

        return SUCCESS;
    }
}
