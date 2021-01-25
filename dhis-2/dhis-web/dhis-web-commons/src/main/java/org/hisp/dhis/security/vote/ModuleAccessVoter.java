package org.hisp.dhis.security.vote;



import com.opensymphony.xwork2.config.entities.ActionConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;


/**
 * AccessDecisionVoter which grants access if one of the granted authorities
 * matches attribute prefix + module name. The module name is taken from an
 * <code>com.opensymphony.xwork.config.entities.ActionConfig</code> object,
 * which is the only type of object this voter supports.
 *
 * @author Torgeir Lorange Ostby
 * @version $Id: ModuleAccessVoter.java 6352 2008-11-20 15:49:52Z larshelg $
 */
@Slf4j
public class ModuleAccessVoter
    extends AbstractPrefixedAccessDecisionVoter
{
    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private Set<String> alwaysAccessible = Collections.emptySet();

    /**
     * Sets a set of names for modules which are always accessible.
     */
    public void setAlwaysAccessible( Set<String> alwaysAccessible )
    {
        this.alwaysAccessible = alwaysAccessible;
    }

    // -------------------------------------------------------------------------
    // AccessDecisionVoter implementation
    // -------------------------------------------------------------------------

    /**
     * Returns true if the class equals
     * <code>com.opensymphony.xwork.config.entities.ActionConfig</code>.
     * False otherwise.
     */
    @Override
    public boolean supports( Class<?> clazz )
    {
        boolean result = ActionConfig.class.equals( clazz );

        log.debug( "Supports class: " + clazz + ", " + result );

        return result;
    }

    /**
     * Votes. Votes ACCESS_ABSTAIN if the object class is not supported. Votes
     * ACCESS_GRANTED if there is a granted authority which equals attribute
     * prefix + module name, or the module name is in the always accessible set.
     * Otherwise votes ACCESS_DENIED.
     */
    @Override
    public int vote( Authentication authentication, Object object, Collection<ConfigAttribute> attributes )
    {
        if ( !supports( object.getClass() ) )
        {
            log.debug( "ACCESS_ABSTAIN [" + object.toString() + "]: Class not supported." );

            return ACCESS_ABSTAIN;
        }

        ActionConfig target = (ActionConfig) object;

        if ( alwaysAccessible.contains( target.getPackageName() ) )
        {
            log.debug( "ACCESS_GRANTED [" + target.getPackageName() + "] by configuration." );

            return ACCESS_GRANTED;
        }

        String requiredAuthority = attributePrefix + target.getPackageName();

        for ( GrantedAuthority grantedAuthority : authentication.getAuthorities() )
        {
            if ( grantedAuthority.getAuthority().equals( requiredAuthority ) )
            {
                log.debug( "ACCESS_GRANTED [" + target.getPackageName() + "]" );

                return ACCESS_GRANTED;
            }
        }

        log.debug( "ACCESS_DENIED [" + target.getPackageName() + "]" );

        return ACCESS_DENIED;
    }
}
