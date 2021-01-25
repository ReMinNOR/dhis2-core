package org.hisp.dhis.settings.user.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.setting.StyleManager;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.user.UserSettingKey;
import org.hisp.dhis.user.UserSettingService;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 */
public class SetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String currentLocale;

    public void setCurrentLocale( String locale )
    {
        this.currentLocale = locale;
    }

    private String currentLocaleDb;

    public void setCurrentLocaleDb( String currentLocaleDb )
    {
        this.currentLocaleDb = currentLocaleDb;
    }

    private String currentStyle;

    public void setCurrentStyle( String style )
    {
        this.currentStyle = style;
    }

    private String analysisDisplayProperty;

    public void setAnalysisDisplayProperty( String analysisDisplayProperty )
    {
        this.analysisDisplayProperty = analysisDisplayProperty;
    }

    private Boolean messageEmailNotification;

    public void setMessageEmailNotification( Boolean messageEmailNotification )
    {
        this.messageEmailNotification = messageEmailNotification;
    }

    private Boolean messageSmsNotification;

    public void setMessageSmsNotification( Boolean messageSmsNotification )
    {
        this.messageSmsNotification = messageSmsNotification;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        localeManager.setCurrentLocale( LocaleUtils.getLocale( currentLocale ) );

        userSettingService.saveUserSetting( UserSettingKey.DB_LOCALE, LocaleUtils.getLocale( currentLocaleDb ) );

        styleManager.setUserStyle( currentStyle );

        userSettingService.saveUserSetting( UserSettingKey.MESSAGE_EMAIL_NOTIFICATION, messageEmailNotification );
        userSettingService.saveUserSetting( UserSettingKey.MESSAGE_SMS_NOTIFICATION, messageSmsNotification );
        userSettingService.saveUserSetting( UserSettingKey.ANALYSIS_DISPLAY_PROPERTY,
            UserSettingKey.getAsRealClass( UserSettingKey.ANALYSIS_DISPLAY_PROPERTY.getName(), analysisDisplayProperty ) );

        message = i18n.getString( "settings_updated" );

        return SUCCESS;
    }
}
