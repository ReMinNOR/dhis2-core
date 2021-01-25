package org.hisp.dhis.useraccount.action;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class UpdateUserProfileAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private UserService userService;

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    private I18nFormat format;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    private String email;

    private String phoneNumber;

    private String introduction;

    private String gender;

    private String birthday;

    private String nationality;

    private String employer;

    private String education;

    private String interests;

    private String languages;

    private String message;

    private String jobTitle;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setIntroduction( String introduction )
    {
        this.introduction = introduction;
    }

    public void setJobTitle( String jobTitle )
    {
        this.jobTitle = jobTitle;
    }

    public void setGender( String gender )
    {
        this.gender = gender;
    }

    public void setBirthday( String birthday )
    {
        this.birthday = birthday;
    }

    public void setNationality( String nationality )
    {
        this.nationality = nationality;
    }

    public void setEmployer( String employer )
    {
        this.employer = employer;
    }

    public void setEducation( String education )
    {
        this.education = education;
    }

    public void setInterests( String interests )
    {
        this.interests = interests;
    }

    public void setLanguages( String languages )
    {
        this.languages = languages;
    }

    public String getMessage()
    {
        return message;
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
        phoneNumber = StringUtils.trimToNull( phoneNumber );
        introduction = StringUtils.trimToNull( introduction );
        jobTitle = StringUtils.trimToNull( jobTitle );
        nationality = StringUtils.trimToNull( nationality );
        employer = StringUtils.trimToNull( employer );
        education = StringUtils.trimToNull( education );
        interests = StringUtils.trimToNull( interests );
        languages = StringUtils.trimToNull( languages );

        User user = userService.getUser( id );

        if ( user == null )
        {
            message = i18n.getString( "user_is_not_available" );

            return ERROR;
        }

        // ---------------------------------------------------------------------
        // Update User
        // ---------------------------------------------------------------------

        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );
        user.setIntroduction( introduction );
        user.setJobTitle( jobTitle );
        user.setGender( gender );
        user.setBirthday( format.parseDate( birthday ) );
        user.setNationality( nationality );
        user.setEmployer( employer );
        user.setEducation( education );
        user.setInterests( interests );
        user.setLanguages( languages );

        userService.updateUser( user );

        message = i18n.getString( "update_user_profile_success" );

        return SUCCESS;
    }
}
