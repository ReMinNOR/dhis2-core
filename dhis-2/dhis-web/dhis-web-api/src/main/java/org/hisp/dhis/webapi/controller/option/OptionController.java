package org.hisp.dhis.webapi.controller.option;



import org.hisp.dhis.option.Option;
import org.hisp.dhis.schema.descriptors.OptionSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = OptionSchemaDescriptor.API_ENDPOINT )
public class OptionController
    extends AbstractCrudController<Option>
{
}
