package org.hisp.dhis.security.action;



import com.google.common.collect.ImmutableMap;
import com.opensymphony.xwork2.Action;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.i18n.ui.resourcebundle.ResourceBundleManager;
import org.hisp.dhis.security.oidc.DhisClientRegistrationRepository;
import org.hisp.dhis.security.oidc.DhisOidcClientRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.mobile.device.DeviceResolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author mortenoh
 */
public class LoginAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DeviceResolver deviceResolver;

    public void setDeviceResolver( DeviceResolver deviceResolver )
    {
        this.deviceResolver = deviceResolver;
    }

    @Autowired
    private ResourceBundleManager resourceBundleManager;

    @Autowired
    private DhisConfigurationProvider configurationProvider;

    @Autowired
    private DhisClientRegistrationRepository repository;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Boolean failed = false;

    public void setFailed( Boolean failed )
    {
        this.failed = failed;
    }

    public Boolean getFailed()
    {
        return failed;
    }

    private List<Locale> availableLocales;

    public List<Locale> getAvailableLocales()
    {
        return availableLocales;
    }

    private final Map<String, Object> oidcConfig = new HashMap<>();

    public Map<String, Object> getOidcConfig()
    {
        return oidcConfig;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        setOidcConfig();

        Device device = deviceResolver.resolveDevice( ServletActionContext.getRequest() );

        ServletActionContext.getResponse().addHeader( "Login-Page", "true" );

        if ( device.isMobile() || device.isTablet() )
        {
            return "mobile";
        }

        availableLocales = new ArrayList<>( resourceBundleManager.getAvailableLocales() );

        return "standard";
    }

    private void setOidcConfig()
    {
        boolean isOidcEnabled = configurationProvider.
            getProperty( ConfigurationKey.OIDC_OAUTH2_LOGIN_ENABLED ).equalsIgnoreCase( "on" );

        if ( !isOidcEnabled )
        {
            return;
        }

        parseRegisteredProviders();
    }

    private void parseRegisteredProviders()
    {
        List<Map<String, String>> providers = new ArrayList<>();

        Set<String> allRegistrationIds = repository.getAllRegistrationId();

        for ( String registrationId : allRegistrationIds )
        {
            DhisOidcClientRegistration clientRegistration = repository.getDhisOidcClientRegistration( registrationId );

            providers.add( ImmutableMap.of(
                "id", registrationId,
                "icon", clientRegistration.getLoginIcon(),
                "iconPadding", clientRegistration.getLoginIconPadding(),
                "loginText", clientRegistration.getLoginText()
            ) );
        }

        oidcConfig.put( "providers", providers );
    }
}
