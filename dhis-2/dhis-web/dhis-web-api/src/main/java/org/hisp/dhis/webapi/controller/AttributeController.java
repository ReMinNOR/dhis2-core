package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.schema.descriptors.AttributeSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = AttributeSchemaDescriptor.API_ENDPOINT )
public class AttributeController
    extends AbstractCrudController<Attribute>
{
}
