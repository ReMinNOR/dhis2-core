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
@RequestMapping( "/type/testInheritedFromBase" )
@ApiVersion( DhisApiVersion.V32 )
public class InheritedFromBaseVersionController extends BaseWithVersionController
{
    @RequestMapping
    public void test( HttpServletResponse response ) throws IOException
    {
        response.getWriter().println();
    }
}
