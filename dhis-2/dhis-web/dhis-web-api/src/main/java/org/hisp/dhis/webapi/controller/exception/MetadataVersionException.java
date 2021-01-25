package org.hisp.dhis.webapi.controller.exception;



/**
 * @author aamerm.
 */
public class MetadataVersionException
    extends Exception
{
    public MetadataVersionException( String message )
    {
        super( message );
    }

    public MetadataVersionException( Throwable cause )
    {
        super( cause );
    }

    public MetadataVersionException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
