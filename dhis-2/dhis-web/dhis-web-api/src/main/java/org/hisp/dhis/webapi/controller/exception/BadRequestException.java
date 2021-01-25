package org.hisp.dhis.webapi.controller.exception;



/**
 * @author  anilkumk.
 */
public class BadRequestException extends Exception
{

    public BadRequestException( String message )
    {
        super( message );
    }

    public BadRequestException( Throwable cause )
    {
        super( cause );
    }

    public BadRequestException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
