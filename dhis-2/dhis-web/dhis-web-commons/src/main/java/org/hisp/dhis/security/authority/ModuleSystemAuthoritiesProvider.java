package org.hisp.dhis.security.authority;



import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.webportal.module.Module;
import org.hisp.dhis.webportal.module.ModuleManager;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: ModuleSystemAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public class ModuleSystemAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private String authorityPrefix;

    public void setAuthorityPrefix( String authorityPrefix )
    {
        this.authorityPrefix = authorityPrefix;
    }

    private Set<String> excludes = new HashSet<>();

    public void setExcludes( Set<String> exclues )
    {
        this.excludes = exclues;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ModuleManager moduleManager;

    public void setModuleManager( ModuleManager moduleManager )
    {
        this.moduleManager = moduleManager;
    }

    // -------------------------------------------------------------------------
    // SystemAuthoritiesProvider implementation
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getSystemAuthorities()
    {
        Set<String> authorities = new HashSet<>();

        for ( Module module : moduleManager.getAllModules() )
        {
            if ( !excludes.contains( module.getName() ) )
            {
                authorities.add( authorityPrefix + module.getName() );
            }
        }

        return authorities;
    }
}
