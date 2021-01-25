package org.hisp.dhis.webapi.controller.user;



import org.hisp.dhis.schema.descriptors.UserCredentialsSchemaDescriptor;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserCredentialsSchemaDescriptor.API_ENDPOINT )
public class UserCredentialsController
    extends AbstractCrudController<UserCredentials>
{
    @Override
    public void postXmlObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpServerErrorException( HttpStatus.BAD_REQUEST );
    }

    @Override
    public void postJsonObject( HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        throw new HttpServerErrorException( HttpStatus.BAD_REQUEST );
    }
}
