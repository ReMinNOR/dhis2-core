package org.hisp.dhis.interceptor;



import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.calendar.CalendarService;
import org.hisp.dhis.configuration.ConfigurationService;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * @author Lars Helge Overland
 */
public class SystemSettingInterceptor
    implements Interceptor
{
    private static final String DATE_FORMAT = "dateFormat";
    private static final String SYSPROP_PORTAL = "runningAsPortal";

    private static final Set<SettingKey> SETTINGS = Sets.newHashSet( SettingKey.APPLICATION_TITLE, SettingKey.APPLICATION_INTRO,
        SettingKey.APPLICATION_NOTIFICATION, SettingKey.APPLICATION_FOOTER, SettingKey.APPLICATION_RIGHT_FOOTER,
        SettingKey.FLAG, SettingKey.START_MODULE, SettingKey.MULTI_ORGANISATION_UNIT_FORMS, SettingKey.ACCOUNT_RECOVERY,
        SettingKey.GOOGLE_ANALYTICS_UA, SettingKey.HELP_PAGE_LINK, SettingKey.REQUIRE_ADD_TO_VIEW, SettingKey.ALLOW_OBJECT_ASSIGNMENT,
        SettingKey.CALENDAR, SettingKey.DATE_FORMAT, SettingKey.RECAPTCHA_SITE );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemSettingManager systemSettingManager;

    public void setSystemSettingManager( SystemSettingManager systemSettingManager )
    {
        this.systemSettingManager = systemSettingManager;
    }

    private ConfigurationService configurationService;

    public void setConfigurationService( ConfigurationService configurationService )
    {
        this.configurationService = configurationService;
    }

    @Autowired
    private CalendarService calendarService;

    // -------------------------------------------------------------------------
    // AroundInterceptor implementation
    // -------------------------------------------------------------------------

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public String intercept( ActionInvocation invocation )
        throws Exception
    {
        Map<String, Object> map = new HashMap<>();
        
        map.put( DATE_FORMAT, calendarService.getSystemDateFormat() );
        map.put( SettingKey.CONFIGURATION.getName(), configurationService.getConfiguration() );
        map.put( SettingKey.FLAG_IMAGE.getName(), systemSettingManager.getFlagImage() );
        map.put( SettingKey.CREDENTIALS_EXPIRES.getName(), systemSettingManager.credentialsExpires() );
        map.put( SettingKey.SELF_REGISTRATION_NO_RECAPTCHA.getName(), systemSettingManager.selfRegistrationNoRecaptcha() );
        map.put( SYSPROP_PORTAL, defaultIfEmpty( System.getProperty( SYSPROP_PORTAL ), String.valueOf( true ) ) );
        
        map.putAll( systemSettingManager.getSystemSettings( SETTINGS ) );
        
        invocation.getStack().push( map );

        return invocation.invoke();
    }
}
