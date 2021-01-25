package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.analytics.AnalyticsTableHook;
import org.hisp.dhis.schema.descriptors.AnalyticsTableHookSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = AnalyticsTableHookSchemaDescriptor.API_ENDPOINT )
public class AnalyticsTableHookController
    extends AbstractCrudController<AnalyticsTableHook>
{
}
