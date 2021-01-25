package org.hisp.dhis.useraccount.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.security.RestoreType;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Lars Helge Overland
 */
public class IsRestoreTokenValidAction
    implements Action
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String username;

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

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

        String errorMessage = securityService
            .verifyRestoreToken( userCredentials, restoreToken, RestoreType.RECOVER_PASSWORD );

        if ( errorMessage != null )
        {
            return ERROR;
        }

        return SUCCESS;
    }
}
