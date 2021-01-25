package org.hisp.dhis.security.authority;



import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.security.access.SecurityMetadataSource;

import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: RequiredAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public interface RequiredAuthoritiesProvider
{
    /**
     * Creates an SecurityMetadataSource based on the required authorities for
     * the action config. The specified action config is set as the secure
     * object. The SecurityMetadataSource may include additional attributes if
     * needed.
     *
     * @param actionConfig the secure actionConfig to get required authorities
     *                     from.
     */
    public SecurityMetadataSource createSecurityMetadataSource( ActionConfig actionConfig );

    /**
     * Creates an SecurityMetadataSource for a specified secure object based on
     * the required authorities for the action config. The
     * SecurityMetadataSource may include additional attributes if needed.
     *
     * @param actionConfig the actionConfig to get required authorities from.
     * @param object       the secure object.
     */
    public SecurityMetadataSource createSecurityMetadataSource( ActionConfig actionConfig, Object object );

    /**
     * Returns all authorities of an action configuration.
     */
    public Collection<String> getAllAuthorities( ActionConfig actionConfig );

    /**
     * Returns the required authorities of an action configuration.
     */
    public Collection<String> getRequiredAuthorities( ActionConfig actionConfig );
}
