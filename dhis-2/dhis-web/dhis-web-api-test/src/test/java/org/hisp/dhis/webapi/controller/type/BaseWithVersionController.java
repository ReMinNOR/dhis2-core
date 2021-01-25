package org.hisp.dhis.webapi.controller.type;



import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@ApiVersion( DhisApiVersion.V31 )
public abstract class BaseWithVersionController
{
    @RequestMapping( value = "/{id}", method = RequestMethod.POST )
    public void testWithId( @PathVariable String id, HttpServletResponse response ) throws IOException
    {
        response.getWriter().println( id );
    }
}
