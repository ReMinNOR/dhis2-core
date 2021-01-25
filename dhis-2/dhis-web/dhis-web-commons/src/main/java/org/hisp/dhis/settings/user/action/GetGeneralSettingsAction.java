package org.hisp.dhis.settings.user.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.i18n.I18nLocaleService;
import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.setting.StyleManager;
import org.hisp.dhis.setting.StyleObject;
import org.hisp.dhis.user.UserSettingKey;
import org.hisp.dhis.user.UserSettingService;

import java.util.List;
import java.util.Locale;

/**
 * @author Chau Thu Tran
 */
public class GetGeneralSettingsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private I18nLocaleService i18nLocaleService;

    public void setI18nLocaleService( I18nLocaleService i18nLocaleService )
    {
        this.i18nLocaleService = i18nLocaleService;
    }

    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    private UserSettingService userSettingService;

    public void setUserSettingService( UserSettingService userSettingService )
    {
        this.userSettingService = userSettingService;
    }

    private StyleManager styleManager;

    public void setStyleManager( StyleManager styleManager )
    {
        this.styleManager = styleManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<Locale> availableLocales;

    public List<Locale> getAvailableLocales()
    {
        return availableLocales;
    }

    private Locale currentLocale;

    public Locale getCurrentLocale()
    {
        return currentLocale;
    }

    private List<Locale> availableLocalesDb;

    public List<Locale> getAvailableLocalesDb()
    {
        return availableLocalesDb;
    }

    private Locale currentLocaleDb;

    public Locale getCurrentLocaleDb()
    {
        return currentLocaleDb;
    }

    private String currentStyle;

    public String getCurrentStyle()
    {
        return currentStyle;
    }

    private List<StyleObject> styles;

    public List<StyleObject> getStyles()
    {
        return styles;
    }

    private String analysisDisplayProperty;

    public String getAnalysisDisplayProperty()
    {
        return analysisDisplayProperty;
    }

    private Boolean messageEmailNotification;

    public Boolean getMessageEmailNotification()
    {
        return messageEmailNotification;
    }

    private Boolean messageSmsNotification;

    public Boolean getMessageSmsNotification()
    {
        return messageSmsNotification;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get available UI locales
        // ---------------------------------------------------------------------

        availableLocales = localeManager.getAvailableLocales();

        currentLocale = localeManager.getCurrentLocale();

        // ---------------------------------------------------------------------
        // Get available DB locales
        // ---------------------------------------------------------------------

        availableLocalesDb = i18nLocaleService.getAllLocales();

        currentLocaleDb = (Locale) userSettingService.getUserSetting( UserSettingKey.DB_LOCALE );

        // ---------------------------------------------------------------------
        // Get styles
        // ---------------------------------------------------------------------

        styles = styleManager.getStyles();

        currentStyle = styleManager.getCurrentStyle();

        analysisDisplayProperty = userSettingService.getUserSetting( UserSettingKey.ANALYSIS_DISPLAY_PROPERTY ).toString();

        messageEmailNotification = (Boolean) userSettingService.getUserSetting( UserSettingKey.MESSAGE_EMAIL_NOTIFICATION );

        messageSmsNotification = (Boolean) userSettingService.getUserSetting( UserSettingKey.MESSAGE_SMS_NOTIFICATION );

        return SUCCESS;
    }
}
