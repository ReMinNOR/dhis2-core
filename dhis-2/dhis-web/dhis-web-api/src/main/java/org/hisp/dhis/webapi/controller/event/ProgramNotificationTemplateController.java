package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.program.notification.ProgramNotificationTemplate;
import org.hisp.dhis.schema.descriptors.ProgramNotificationTemplateSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Halvdan Hoem Grelland
 */
@Controller
@RequestMapping( value = ProgramNotificationTemplateSchemaDescriptor.API_ENDPOINT )
@ApiVersion( include = { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ProgramNotificationTemplateController
    extends AbstractCrudController<ProgramNotificationTemplate>
{
}
