package org.hisp.dhis.webapi.controller.exception;



/**
 * Created by sultanm.
 * This exception could be used in all operation forbidden cases
 */
public class OperationNotAllowedException
    extends Exception
{

    public OperationNotAllowedException( String message )
    {
        super( message );
    }

    public OperationNotAllowedException( Throwable cause )
    {
        super( cause );
    }

    public OperationNotAllowedException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
