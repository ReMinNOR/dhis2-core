package org.hisp.dhis.security.authority;



import java.util.Collection;
import java.util.Collections;
import java.util.Set;

/**
 * Simple SystemAuthoritiesProvider where the system authorities are simply set
 * using a setter method. 
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: SimpleSystemAuthoritiesProvider.java 3516 2007-08-05 11:45:54Z torgeilo $
 */
public class SimpleSystemAuthoritiesProvider
    implements SystemAuthoritiesProvider
{
    private Set<String> authorities = Collections.emptySet();

    public void setAuthorities( Set<String> authorities )
    {
        this.authorities = authorities;
    }

    // -------------------------------------------------------------------------
    // Interface implementation
    // -------------------------------------------------------------------------

    @Override
    public Collection<String> getSystemAuthorities()
    {
        return authorities;
    }
}
