package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.programrule.ProgramRuleAction;
import org.hisp.dhis.schema.descriptors.ProgramRuleActionSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author markusbekken
 */
@Controller
@RequestMapping( value = ProgramRuleActionSchemaDescriptor.API_ENDPOINT )
public class ProgramRuleActionController
    extends AbstractCrudController<ProgramRuleAction>
{
}
