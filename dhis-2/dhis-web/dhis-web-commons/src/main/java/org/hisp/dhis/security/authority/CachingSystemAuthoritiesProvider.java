package org.hisp.dhis.security.authority;



import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: CachingSystemAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public class CachingSystemAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemAuthoritiesProvider source;

    public void setSource( SystemAuthoritiesProvider source )
    {
        this.source = source;
    }

    public CachingSystemAuthoritiesProvider( SystemAuthoritiesProvider source )
    {
        this.source = source;
    }

// -------------------------------------------------------------------------
    // SystemAuthoritiesProvider implementation
    // -------------------------------------------------------------------------

    private Collection<String> cache;

    @Override
    public Collection<String> getSystemAuthorities()
    {
        if ( cache == null )
        {
            cache = source.getSystemAuthorities();
        }

        return cache;
    }
}
