package org.hisp.dhis.webapi.controller.validation;



import org.hisp.dhis.schema.descriptors.ValidationRuleGroupSchemaDescriptor;
import org.hisp.dhis.validation.ValidationRuleGroup;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ValidationRuleGroupSchemaDescriptor.API_ENDPOINT )
public class ValidationRuleGroupController
    extends AbstractCrudController<ValidationRuleGroup>
{
}
