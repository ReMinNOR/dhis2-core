package org.hisp.dhis.webapi.controller.dataelement;



import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.schema.descriptors.DataElementSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = DataElementSchemaDescriptor.API_ENDPOINT )
public class DataElementController
    extends AbstractCrudController<DataElement>
{
}
