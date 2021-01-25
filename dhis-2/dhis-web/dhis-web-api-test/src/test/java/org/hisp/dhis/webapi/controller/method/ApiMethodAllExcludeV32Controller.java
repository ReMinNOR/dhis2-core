package org.hisp.dhis.webapi.controller.method;



import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( "/method/testAllExcludeV32" )
public class ApiMethodAllExcludeV32Controller
{
    @RequestMapping( "a" )
    @ApiVersion( value = DhisApiVersion.ALL, exclude = DhisApiVersion.V32 )
    public void testAllA( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }

    @RequestMapping( "b" )
    @ApiVersion( value = DhisApiVersion.ALL, exclude = DhisApiVersion.V32 )
    public void testAllB( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }
}
