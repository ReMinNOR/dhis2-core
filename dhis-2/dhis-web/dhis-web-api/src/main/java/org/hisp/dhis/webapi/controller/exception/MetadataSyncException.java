package org.hisp.dhis.webapi.controller.exception;



/**
 * @author anilkumk.
 */
public class MetadataSyncException
    extends Exception
{
    public MetadataSyncException( String message )
    {
        super( message );
    }

    public MetadataSyncException( Throwable cause )
    {
        super( cause );
    }

    public MetadataSyncException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
