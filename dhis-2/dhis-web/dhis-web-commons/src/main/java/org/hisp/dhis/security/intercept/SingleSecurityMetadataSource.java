package org.hisp.dhis.security.intercept;



import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityMetadataSource;

/**
 * Generic SecurityMetadataSource for one single object.
 * 
 * @author Torgeir Lorange Ostby
 */
public class SingleSecurityMetadataSource
    implements SecurityMetadataSource
{
    private Object object;

    private Collection<ConfigAttribute> attributes;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public SingleSecurityMetadataSource( Object object )
    {
        this.object = object;
    }

    public SingleSecurityMetadataSource( Object object, Collection<ConfigAttribute> attributes )
    {
        this.object = object;   
        this.attributes = attributes;        
    }

    // -------------------------------------------------------------------------
    // SecurityMetadataSource implementation
    // -------------------------------------------------------------------------

    @Override
    public Collection<ConfigAttribute> getAttributes( Object object )
        throws IllegalArgumentException
    {
        if ( !supports( object.getClass() ) )
        {
            throw new IllegalArgumentException( "Illegal type of object: " + object.getClass() );
        }

        if ( object.equals( this.object ) )
        {
            return attributes;
        }

        return null;
    }

    @Override
    public boolean supports( Class<?> clazz )
    {
        return clazz.isAssignableFrom( object.getClass() );
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes()
    {
        return this.attributes;
    }

}
