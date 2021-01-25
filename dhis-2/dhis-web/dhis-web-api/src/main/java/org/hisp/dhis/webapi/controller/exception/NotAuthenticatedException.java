package org.hisp.dhis.webapi.controller.exception;



/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class NotAuthenticatedException extends Exception
{
    public NotAuthenticatedException()
    {
        super( "User object is null, user is not authenticated." );
    }
}
