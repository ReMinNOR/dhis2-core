package org.hisp.dhis.security.vote;



import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * AccessDecisionManager which delegates to other AccessDecisionManagers in a
 * logical or fashion. Delegation is stopped at the first positive answer from
 * the delegates, where the order of execution is defined by the list of
 * AccessDecisionManagers. So if the first AccessDecisionManager grants access
 * for a specific target, no other AccessDecisionManager is questioned.
 *
 * @author Torgeir Lorange Ostby
 * @version $Id: LogicalOrAccessDecisionManager.java 6335 2008-11-20 11:11:26Z larshelg $
 */
@Primary
@Component
@Slf4j
public class LogicalOrAccessDecisionManager implements AccessDecisionManager
{
    private List<AccessDecisionManager> accessDecisionManagers;

    public LogicalOrAccessDecisionManager(
        List<AccessDecisionManager> accessDecisionManagers )
    {
        if ( accessDecisionManagers == null )
        {
            accessDecisionManagers = Collections.emptyList();
        }
        this.accessDecisionManagers = accessDecisionManagers;
    }

    // -------------------------------------------------------------------------
    // Interface implementation
    // -------------------------------------------------------------------------

    @Override
    public void decide( Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes )
        throws AccessDeniedException, InsufficientAuthenticationException
    {
        AccessDeniedException ade = null;
        InsufficientAuthenticationException iae = null;

        for ( AccessDecisionManager accessDecisionManager : accessDecisionManagers )
        {
            // Cannot assume that all decision managers can support the same type

            if ( accessDecisionManager.supports( object.getClass() ) )
            {
                try
                {
                    accessDecisionManager.decide( authentication, object, configAttributes );

                    log.debug( "ACCESS GRANTED [" + object.toString() + "]" );

                    return;
                }
                catch ( AccessDeniedException e )
                {
                    ade = e;
                }
                catch ( InsufficientAuthenticationException e )
                {
                    iae = e;
                }
            }
        }

        log.debug( "ACCESS DENIED [" + object.toString() + "]" );

        if ( ade != null )
        {
            throw ade;
        }

        if ( iae != null )
        {
            throw iae;
        }
    }

    @Override
    public boolean supports( ConfigAttribute configAttribute )
    {
        for ( AccessDecisionManager accessDecisionManager : accessDecisionManagers )
        {
            if ( accessDecisionManager.supports( configAttribute ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean supports( Class<?> clazz )
    {
        for ( AccessDecisionManager accessDecisionManager : accessDecisionManagers )
        {
            if ( accessDecisionManager.supports( clazz ) )
            {
                return true;
            }
        }

        return false;
    }
}
