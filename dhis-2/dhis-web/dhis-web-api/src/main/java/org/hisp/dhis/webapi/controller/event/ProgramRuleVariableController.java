package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.programrule.ProgramRuleVariable;
import org.hisp.dhis.schema.descriptors.ProgramRuleVariableSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author markusbekken
 */
@Controller
@RequestMapping( value = ProgramRuleVariableSchemaDescriptor.API_ENDPOINT )
public class ProgramRuleVariableController
    extends AbstractCrudController<ProgramRuleVariable>
{
}
