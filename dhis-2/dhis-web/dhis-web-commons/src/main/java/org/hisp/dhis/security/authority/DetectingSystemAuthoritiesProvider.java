package org.hisp.dhis.security.authority;



import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;
import org.apache.struts2.dispatcher.Dispatcher;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DetectingSystemAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public class DetectingSystemAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RequiredAuthoritiesProvider requiredAuthoritiesProvider;

    public void setRequiredAuthoritiesProvider( RequiredAuthoritiesProvider requiredAuthoritiesProvider )
    {
        this.requiredAuthoritiesProvider = requiredAuthoritiesProvider;
    }

    // -------------------------------------------------------------------------
    // SystemAuthoritiesProvider implementation
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getSystemAuthorities()
    {
        HashSet<String> authorities = new HashSet<>();

        Configuration configuration = Dispatcher.getInstance().getConfigurationManager().getConfiguration();

        for ( PackageConfig packageConfig : configuration.getPackageConfigs().values() )
        {
            for ( ActionConfig actionConfig : packageConfig.getActionConfigs().values() )
            {
                authorities.addAll( requiredAuthoritiesProvider.getAllAuthorities( actionConfig ) );
            }
        }

        return authorities;
    }
}
