package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.schema.descriptors.ProgramIndicatorGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.program.ProgramIndicatorGroup;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Mark Polak
 */
@Controller
@RequestMapping( value = ProgramIndicatorGroupSchemaDescriptor.API_ENDPOINT )
public class ProgramIndicatorGroupController
    extends AbstractCrudController<ProgramIndicatorGroup>
{
}
