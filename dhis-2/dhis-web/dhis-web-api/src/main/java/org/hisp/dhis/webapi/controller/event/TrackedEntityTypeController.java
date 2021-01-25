package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.schema.descriptors.TrackedEntityTypeSchemaDescriptor;
import org.hisp.dhis.trackedentity.TrackedEntityType;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = TrackedEntityTypeSchemaDescriptor.API_ENDPOINT )
public class TrackedEntityTypeController
    extends AbstractCrudController<TrackedEntityType>
{
}
