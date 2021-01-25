package org.hisp.dhis.security.vote;



import com.opensymphony.xwork2.config.entities.ActionConfig;
import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.security.StrutsAuthorityUtils;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: ActionAccessVoter.java 6335 2008-11-20 11:11:26Z larshelg $
 */
@Slf4j
public class ActionAccessVoter
    extends AbstractPrefixedAccessDecisionVoter
{

    // -------------------------------------------------------------------------
    // AccessDecisionVoter Input
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

    // -------------------------------------------------------------------------
    // AccessDecisionVoter implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean supports( Class<?> clazz )
    {
        boolean result = ActionConfig.class.equals( clazz );

        log.debug( "Supports class: " + clazz + ", " + result );

        return result;
    }

    @Override
    public int vote( Authentication authentication, Object object, Collection<ConfigAttribute> attributes )
    {
        if ( !supports( object.getClass() ) )
        {
            log.debug( "ACCESS_ABSTAIN [" + object.toString() + "]: Class not supported." );

            return AccessDecisionVoter.ACCESS_ABSTAIN;
        }

        ActionConfig actionConfig = (ActionConfig) object;
        Collection<ConfigAttribute> requiredAuthorities = StrutsAuthorityUtils
            .getConfigAttributes( actionConfig, requiredAuthoritiesKey );
        Collection<ConfigAttribute> anyAuthorities = StrutsAuthorityUtils
            .getConfigAttributes( actionConfig, anyAuthoritiesKey );

        int allStatus = allAuthorities( authentication, object, requiredAuthorities );

        if ( allStatus == AccessDecisionVoter.ACCESS_DENIED )
        {
            return AccessDecisionVoter.ACCESS_DENIED;
        }

        int anyStatus = anyAuthority( authentication, object, anyAuthorities );

        if ( anyStatus == AccessDecisionVoter.ACCESS_DENIED )
        {
            return AccessDecisionVoter.ACCESS_DENIED;
        }

        if ( allStatus == AccessDecisionVoter.ACCESS_GRANTED || anyStatus == AccessDecisionVoter.ACCESS_GRANTED )
        {
            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    private int allAuthorities( Authentication authentication, Object object, Collection<ConfigAttribute> attributes )
    {
        int supported = 0;

        for ( ConfigAttribute attribute : attributes )
        {
            if ( supports( attribute ) )
            {
                ++supported;
                boolean found = false;

                for ( GrantedAuthority authority : authentication.getAuthorities() )
                {
                    if ( authority.getAuthority().equals( attribute.getAttribute() ) )
                    {
                        found = true;
                        break;
                    }
                }

                if ( !found )
                {
                    log.debug( "ACCESS_DENIED [" + object.toString() + "]" );

                    return AccessDecisionVoter.ACCESS_DENIED;
                }
            }
        }

        if ( supported > 0 )
        {
            log.debug( "ACCESS_GRANTED [" + object.toString() + "]" );

            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        log.debug( "ACCESS_ABSTAIN [" + object.toString() + "]: No supported attributes." );

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }

    private int anyAuthority( Authentication authentication, Object object, Collection<ConfigAttribute> attributes )
    {
        int supported = 0;
        boolean found = false;

        for ( ConfigAttribute attribute : attributes )
        {
            if ( supports( attribute ) )
            {
                ++supported;

                for ( GrantedAuthority authority : authentication.getAuthorities() )
                {
                    if ( authority.getAuthority().equals( attribute.getAttribute() ) )
                    {
                        found = true;
                        break;
                    }
                }

            }
        }

        if ( !found && supported > 0 )
        {
            log.debug( "ACCESS_DENIED [" + object.toString() + "]" );

            return AccessDecisionVoter.ACCESS_DENIED;
        }

        if ( supported > 0 )
        {
            log.debug( "ACCESS_GRANTED [" + object.toString() + "]" );

            return AccessDecisionVoter.ACCESS_GRANTED;
        }

        log.debug( "ACCESS_ABSTAIN [" + object.toString() + "]: No supported attributes." );

        return AccessDecisionVoter.ACCESS_ABSTAIN;
    }
}
