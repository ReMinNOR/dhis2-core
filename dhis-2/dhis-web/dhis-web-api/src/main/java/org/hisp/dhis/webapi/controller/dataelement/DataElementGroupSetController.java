package org.hisp.dhis.webapi.controller.dataelement;



import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.schema.descriptors.DataElementGroupSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = DataElementGroupSetSchemaDescriptor.API_ENDPOINT )
public class DataElementGroupSetController
    extends AbstractCrudController<DataElementGroupSet>
{
}
