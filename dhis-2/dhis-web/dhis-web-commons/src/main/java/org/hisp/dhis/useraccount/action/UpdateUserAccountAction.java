package org.hisp.dhis.useraccount.action;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateUserAccountAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    private PasswordManager passwordManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private I18n i18n;

    private Integer id;

    private String oldPassword;

    private String rawPassword;

    private String surname;

    private String firstName;

    private String email;

    private String phoneNumber;

    private String message;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setOldPassword( String oldPassword )
    {
        this.oldPassword = oldPassword;
    }

    public void setRawPassword( String rawPassword )
    {
        this.rawPassword = rawPassword;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        email = StringUtils.trimToNull( email );
        rawPassword = StringUtils.trimToNull( rawPassword );

        User user = userService.getUser( id );
        UserCredentials credentials = user.getUserCredentials();
        
        String currentPassword = credentials.getPassword();

        // ---------------------------------------------------------------------
        // Deny update if user has local authentication and password is wrong
        // ---------------------------------------------------------------------

        if ( !credentials.isExternalAuth() && !passwordManager.matches( oldPassword, currentPassword ) )
        {
            message = i18n.getString( "wrong_password" );
            return INPUT;
        }

        // ---------------------------------------------------------------------
        // Update userCredentials and user
        // ---------------------------------------------------------------------

        user.setSurname( surname );
        user.setFirstName( firstName );
        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );
        
        userService.encodeAndSetPassword( user, rawPassword );
        
        userService.updateUserCredentials( credentials );
        userService.updateUser( user );

        message = i18n.getString( "update_user_success" );

        return SUCCESS;
    }
}
