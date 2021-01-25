package org.hisp.dhis.security;



/**
 * @author Torgeir Lorange Ostby
 */
public interface ActionAccessResolver
{
    boolean hasAccess( String namespace, String name );
}
