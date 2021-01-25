package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.program.ProgramTrackedEntityAttributeGroup;
import org.hisp.dhis.schema.descriptors.ProgramTrackedEntityAttributeGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Viet Nguyen
 */
@Controller
@RequestMapping( value = ProgramTrackedEntityAttributeGroupSchemaDescriptor.API_ENDPOINT )
public class ProgramTrackedEntityAttributeGroupController
    extends AbstractCrudController<ProgramTrackedEntityAttributeGroup>
{
}
