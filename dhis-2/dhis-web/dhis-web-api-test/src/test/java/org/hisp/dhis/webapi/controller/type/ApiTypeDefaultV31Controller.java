package org.hisp.dhis.webapi.controller.type;



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
@RequestMapping( "/type/testDefaultV31" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.V31 } )
public class ApiTypeDefaultV31Controller
{
    @RequestMapping
    public void test( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( "TEST" );
    }
}
