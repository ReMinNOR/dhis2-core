package org.hisp.dhis.webapi.controller.validation;



import org.hisp.dhis.schema.descriptors.ValidationNotificationTemplateSchemaDescriptor;
import org.hisp.dhis.validation.notification.ValidationNotificationTemplate;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Halvdan Hoem Grelland
 */
@Controller
@RequestMapping( value = ValidationNotificationTemplateSchemaDescriptor.API_ENDPOINT )
@ApiVersion( include = { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ValidationNotificationTemplateController
    extends AbstractCrudController<ValidationNotificationTemplate>
{
}
