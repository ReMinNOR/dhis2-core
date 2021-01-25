package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.trackedentityfilter.TrackedEntityInstanceFilter;
import org.hisp.dhis.schema.descriptors.TrackedEntityInstanceFilterSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Abyot Asalefew Gizaw <abyota@gmail.com>
 *
 */
@Controller
@RequestMapping( value = TrackedEntityInstanceFilterSchemaDescriptor.API_ENDPOINT )
public class TrackedEntityInstanceFilterController
    extends AbstractCrudController<TrackedEntityInstanceFilter>
{
}
