package org.hisp.dhis.webapi.controller.exception;



/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class FilterTooShortException extends Exception
{
    public FilterTooShortException()
    {
        super( "Required String parameter 'filter' must be at least 3 characters in length." );
    }
}
