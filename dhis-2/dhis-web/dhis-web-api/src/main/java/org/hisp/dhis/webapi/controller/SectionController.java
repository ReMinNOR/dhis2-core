package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.schema.descriptors.SectionSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = SectionSchemaDescriptor.API_ENDPOINT )
public class SectionController
    extends AbstractCrudController<Section>
{
}
