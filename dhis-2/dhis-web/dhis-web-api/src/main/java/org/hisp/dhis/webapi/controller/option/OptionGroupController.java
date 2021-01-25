package org.hisp.dhis.webapi.controller.option;



import org.hisp.dhis.option.OptionGroup;
import org.hisp.dhis.schema.descriptors.OptionGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */

@Controller
@RequestMapping( value = OptionGroupSchemaDescriptor.API_ENDPOINT )
public class OptionGroupController
    extends AbstractCrudController<OptionGroup>
{
}
