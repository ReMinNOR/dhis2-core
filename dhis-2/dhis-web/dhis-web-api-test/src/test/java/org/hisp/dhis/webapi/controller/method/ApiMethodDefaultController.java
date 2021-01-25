package org.hisp.dhis.webapi.controller.method;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( "/method/testDefault" )
public class ApiMethodDefaultController
{
    @RequestMapping( "a" )
    @ApiVersion( DhisApiVersion.DEFAULT )
    public void testVDefaultA( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }

    @RequestMapping( "b" )
    @ApiVersion( DhisApiVersion.DEFAULT )
    public void testDefaultB( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }
}
