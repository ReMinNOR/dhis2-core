package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.program.ProgramStageSection;
import org.hisp.dhis.schema.descriptors.ProgramStageSectionSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ProgramStageSectionSchemaDescriptor.API_ENDPOINT )
public class ProgramStageSectionController
    extends AbstractCrudController<ProgramStageSection>
{
}
