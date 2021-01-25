package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.schema.descriptors.ProgramStageSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ProgramStageSchemaDescriptor.API_ENDPOINT )
public class ProgramStageController
    extends AbstractCrudController<ProgramStage>
{
}
