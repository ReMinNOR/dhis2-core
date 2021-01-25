package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.program.ProgramSection;
import org.hisp.dhis.schema.descriptors.ProgramSectionSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Henning Håkonsen
 */
@Controller
@RequestMapping( value = ProgramSectionSchemaDescriptor.API_ENDPOINT )
public class ProgramSectionController
    extends AbstractCrudController<ProgramSection>
{
}
