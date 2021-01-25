package org.hisp.dhis.security;





import lombok.extern.slf4j.Slf4j;
import org.apache.struts2.dispatcher.Dispatcher;
import org.hisp.dhis.security.authority.RequiredAuthoritiesProvider;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.SecurityMetadataSource;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.opensymphony.xwork2.config.Configuration;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.config.entities.PackageConfig;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SpringSecurityActionAccessResolver.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
@Slf4j
public class SpringSecurityActionAccessResolver
    implements ActionAccessResolver
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private RequiredAuthoritiesProvider requiredAuthoritiesProvider;

    public void setRequiredAuthoritiesProvider( RequiredAuthoritiesProvider requiredAuthoritiesProvider )
    {
        this.requiredAuthoritiesProvider = requiredAuthoritiesProvider;
    }

    private AccessDecisionManager accessDecisionManager;

    public void setAccessDecisionManager( AccessDecisionManager accessDecisionManager )
    {
        this.accessDecisionManager = accessDecisionManager;
    }

    // -------------------------------------------------------------------------
    // ActionAccessResolver implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean hasAccess( String module, String name )
    {
        // ---------------------------------------------------------------------
        // Get ObjectDefinitionSource
        // ---------------------------------------------------------------------

        Configuration config = Dispatcher.getInstance().getConfigurationManager().getConfiguration();

        PackageConfig packageConfig = config.getPackageConfig( module );

        if ( packageConfig == null )
        {
            throw new IllegalArgumentException( "Module doesn't exist: '" + module + "'" );
        }

        ActionConfig actionConfig = packageConfig.getActionConfigs().get( name );

        if ( actionConfig == null )
        {
            throw new IllegalArgumentException( "Module " + module + " doesn't have an action named: '" + name + "'" );
        }

        SecurityMetadataSource securityMetadataSource = requiredAuthoritiesProvider
            .createSecurityMetadataSource( actionConfig );

        // ---------------------------------------------------------------------
        // Test access
        // ---------------------------------------------------------------------

        SecurityContext securityContext = SecurityContextHolder.getContext();

        Authentication authentication = securityContext.getAuthentication();

        try
        {
            if ( securityMetadataSource.getAttributes( actionConfig ) != null )
            {
                if ( authentication == null || !authentication.isAuthenticated() )
                {
                    return false;
                }

                accessDecisionManager.decide( authentication, actionConfig, securityMetadataSource
                    .getAttributes( actionConfig ) );
            }

            log.debug( "Access to [" + module + ", " + name + "]: TRUE" );

            return true;
        }
        catch ( AccessDeniedException e )
        {
            log.debug( "Access to [" + module + ", " + name + "]: FALSE (access denied)" );

            return false;
        }
        catch ( InsufficientAuthenticationException e )
        {
            log.debug( "Access to [" + module + ", " + name + "]: FALSE (insufficient authentication)" );

            return false;
        }
    }
}
