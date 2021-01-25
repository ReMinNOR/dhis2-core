package org.hisp.dhis.security.vote;





import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: AbstractPrefixedAccessDecisionVoter.java 3160 2007-03-24 20:15:06Z torgeilo $
 */
@Slf4j
public abstract class AbstractPrefixedAccessDecisionVoter
    implements AccessDecisionVoter<Object>
{
    // -------------------------------------------------------------------------
    // Prefix
    // -------------------------------------------------------------------------

    protected String attributePrefix = "";

    public void setAttributePrefix( String attributePrefix )
    {
        this.attributePrefix = attributePrefix;
    }

    // -------------------------------------------------------------------------
    // AccessDecisionVoter implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean supports( ConfigAttribute configAttribute )
    {
        boolean result = configAttribute.getAttribute() != null
            && configAttribute.getAttribute().startsWith( attributePrefix );

        log.debug( "Supports configAttribute: " + configAttribute + ", " + result + " (" + getClass().getSimpleName()
            + ")" );

        return result;
    }
}
