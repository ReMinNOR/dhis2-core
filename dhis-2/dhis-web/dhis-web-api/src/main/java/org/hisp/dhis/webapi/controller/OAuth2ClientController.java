package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.schema.descriptors.OAuth2ClientSchemaDescriptor;
import org.hisp.dhis.security.oauth2.OAuth2Client;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OAuth2ClientSchemaDescriptor.API_ENDPOINT )
public class OAuth2ClientController
    extends AbstractCrudController<OAuth2Client>
{
}
