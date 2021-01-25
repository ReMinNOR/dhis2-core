package org.hisp.dhis.security.authority;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.security.StrutsAuthorityUtils;
import org.hisp.dhis.security.intercept.SingleSecurityMetadataSource;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

import com.opensymphony.xwork2.config.entities.ActionConfig;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultRequiredAuthoritiesProvider.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
public class DefaultRequiredAuthoritiesProvider
    implements RequiredAuthoritiesProvider
{
    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private String requiredAuthoritiesKey;

    public void setRequiredAuthoritiesKey( String requiredAuthoritiesKey )
    {
        this.requiredAuthoritiesKey = requiredAuthoritiesKey;
    }

    private String anyAuthoritiesKey;

    public void setAnyAuthoritiesKey( String anyAuthoritiesKey )
    {
        this.anyAuthoritiesKey = anyAuthoritiesKey;
    }

    private Set<String> globalAttributes = Collections.emptySet();

    public void setGlobalAttributes( Set<String> globalAttributes )
    {
        this.globalAttributes = globalAttributes;
    }

    // -------------------------------------------------------------------------
    // RequiredAuthoritiesProvider implementation
    // -------------------------------------------------------------------------

    @Override
    public SecurityMetadataSource createSecurityMetadataSource( ActionConfig actionConfig )
    {
        return createSecurityMetadataSource( actionConfig, actionConfig );
    }

    @Override
    public SecurityMetadataSource createSecurityMetadataSource( ActionConfig actionConfig, Object object )
    {
        Collection<ConfigAttribute> attributes = new ArrayList<>();
        attributes.addAll( StrutsAuthorityUtils.getConfigAttributes( getRequiredAuthorities( actionConfig ) ) );
        attributes.addAll( StrutsAuthorityUtils.getConfigAttributes( globalAttributes ) );

        return new SingleSecurityMetadataSource( object, attributes );
    }

    @Override
    public Collection<String> getAllAuthorities( ActionConfig actionConfig )
    {
        Collection<String> authorities = new HashSet<>();
        authorities.addAll( getRequiredAuthorities( actionConfig ) );
        authorities.addAll( getAnyAuthorities( actionConfig ) );

        return authorities;
    }

    @Override
    public Collection<String> getRequiredAuthorities( ActionConfig actionConfig )
    {
        return StrutsAuthorityUtils.getAuthorities( actionConfig, requiredAuthoritiesKey );
    }

    public Collection<String> getAnyAuthorities( ActionConfig actionConfig )
    {
        return StrutsAuthorityUtils.getAuthorities( actionConfig, anyAuthoritiesKey );
    }
}
