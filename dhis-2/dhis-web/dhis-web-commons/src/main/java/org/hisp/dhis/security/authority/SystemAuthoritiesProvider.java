package org.hisp.dhis.security.authority;



import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: SystemAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public interface SystemAuthoritiesProvider
{
    String ID = SystemAuthoritiesProvider.class.getName();

    Collection<String> getSystemAuthorities();
}
