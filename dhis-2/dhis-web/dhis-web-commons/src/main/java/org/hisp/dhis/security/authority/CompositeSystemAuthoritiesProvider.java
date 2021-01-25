package org.hisp.dhis.security.authority;



import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: CompositeSystemAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public class CompositeSystemAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private Set<SystemAuthoritiesProvider> sources = new HashSet<>();

    public void setSources( Set<SystemAuthoritiesProvider> sources )
    {
        this.sources = sources;
    }

    // -------------------------------------------------------------------------
    // SystemAuthoritiesProvider implementation
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getSystemAuthorities()
    {
        Set<String> authorities = new HashSet<>();

        for ( SystemAuthoritiesProvider source : sources )
        {
            authorities.addAll( source.getSystemAuthorities() );
        }

        return authorities;
    }
}
