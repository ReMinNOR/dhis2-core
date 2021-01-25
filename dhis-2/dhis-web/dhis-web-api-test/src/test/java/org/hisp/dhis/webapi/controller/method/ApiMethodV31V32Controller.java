package org.hisp.dhis.webapi.controller.method;



import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( "/method/testV31V32" )
public class ApiMethodV31V32Controller
{
    @RequestMapping( "a" )
    @ApiVersion( DhisApiVersion.V31 )
    public void testV31( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }

    @RequestMapping( value = "a", method = RequestMethod.POST )
    @ApiVersion( DhisApiVersion.V31 )
    public void testPostV31( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }

    @RequestMapping( "b" )
    @ApiVersion( DhisApiVersion.V32 )
    public void testV32( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }

    @RequestMapping( value = "b", method = RequestMethod.PUT )
    @ApiVersion( DhisApiVersion.V32 )
    public void testPutV32( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }
}
