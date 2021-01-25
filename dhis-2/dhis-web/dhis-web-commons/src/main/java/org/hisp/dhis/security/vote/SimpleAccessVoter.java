package org.hisp.dhis.security.vote;



import java.util.Collection;


import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

/**
 * Simple AccessDecisionVoter which grants access if a specified required
 * authority is among the configAttributes.
 * 
 * @author Torgeir Lorange Ostby
 * @version $Id: SimpleAccessVoter.java 6352 2008-11-20 15:49:52Z larshelg $
 */
@Slf4j
public class SimpleAccessVoter
    implements AccessDecisionVoter<Object>
{
    private String requiredAuthority;

    public void setRequiredAuthority( String requiredAuthority )
    {
        this.requiredAuthority = requiredAuthority;
    }

    // -------------------------------------------------------------------------
    // Interface implementation
    // -------------------------------------------------------------------------

    public SimpleAccessVoter( String requiredAuthority )
    {
        this.requiredAuthority = requiredAuthority;
    }

    @Override
    public boolean supports( ConfigAttribute configAttribute )
    {
        return configAttribute != null && configAttribute.getAttribute() != null
            && configAttribute.getAttribute().equals( requiredAuthority );
    }

    @Override
    public boolean supports( Class<?> clazz )
    {
        return true;
    }

    @Override
    public int vote( Authentication authentication, Object object, Collection<ConfigAttribute> attributes )
    {
        for ( GrantedAuthority authority : authentication.getAuthorities() )
        {
            if ( authority.getAuthority().equals( requiredAuthority ) )
            {
                log.debug( "ACCESS GRANTED [" + object.toString() + "]" );

                return ACCESS_GRANTED;
            }
        }

        log.debug( "ACCESS DENIED [" + object.toString() + "]" );

        return ACCESS_DENIED;
    }
}
