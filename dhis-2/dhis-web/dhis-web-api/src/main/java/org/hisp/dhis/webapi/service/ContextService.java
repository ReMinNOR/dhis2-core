package org.hisp.dhis.webapi.service;



import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public interface ContextService
{
    /**
     * Get full path of servlet.
     *
     * @return Full HREF to servlet
     * @see javax.servlet.http.HttpServletRequest
     */
    String getServletPath();

    /**
     * Get HREF to context.
     *
     * @return Full HREF to context (context root)
     * @see javax.servlet.http.HttpServletRequest
     */
    String getContextPath();

    /**
     * Get HREF to Web-API.
     *
     * @return Full HREF to Web-API
     * @see javax.servlet.http.HttpServletRequest
     */
    String getApiPath();

    /**
     * Get active HttpServletRequest
     *
     * @return HttpServletRequest
     */
    HttpServletRequest getRequest();

    /**
     * Returns a list of values from a parameter, if the parameter doesn't exist, it will
     * return a empty list.
     *
     * @param name Parameter to get
     * @return List of parameter values, or empty if not found
     */
    List<String> getParameterValues( String name );

    /**
     * Get all parameters as a map of key => values, supports more than one pr key (so values is a collection)
     */
    Map<String, List<String>> getParameterValuesMap();

    /**
     * Get a list of fields from request
     */
    List<String> getFieldsFromRequestOrAll();

    List<String> getFieldsFromRequestOrElse( String s );
}
