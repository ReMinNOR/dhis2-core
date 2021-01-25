package org.hisp.dhis.webapi.controller.option;



import org.hisp.dhis.option.OptionGroupSet;
import org.hisp.dhis.schema.descriptors.OptionGroupSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */

@Controller
@RequestMapping( value = OptionGroupSetSchemaDescriptor.API_ENDPOINT )
public class OptionGroupSetController
    extends AbstractCrudController<OptionGroupSet>
{
}
