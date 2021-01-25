package org.hisp.dhis.webapi.controller.sms;



import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.schema.descriptors.SmsCommandSchemaDescriptor;
import org.hisp.dhis.sms.command.SMSCommand;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zubair@dhis2.org on 18.08.17.
 */

@Controller
@RequestMapping( value = SmsCommandSchemaDescriptor.API_ENDPOINT )
@ApiVersion( include = { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class SmsCommandController
    extends AbstractCrudController<SMSCommand>
{
}
