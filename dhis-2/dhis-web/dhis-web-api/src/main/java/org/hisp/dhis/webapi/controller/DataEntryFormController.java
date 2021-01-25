package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dataentryform.DataEntryForm;
import org.hisp.dhis.schema.descriptors.DataEntryFormSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = DataEntryFormSchemaDescriptor.API_ENDPOINT )
public class DataEntryFormController
    extends AbstractCrudController<DataEntryForm>
{
}
