package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dataset.notifications.DataSetNotificationTemplate;
import org.hisp.dhis.schema.descriptors.DataSetNotificationTemplateSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;



/**
 * Created by zubair on 02.07.17.
 */
@Controller
@RequestMapping( value = DataSetNotificationTemplateSchemaDescriptor.API_ENDPOINT )
@ApiVersion( include = { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class DataSetNotificationTemplateController extends
    AbstractCrudController<DataSetNotificationTemplate>
{
}
