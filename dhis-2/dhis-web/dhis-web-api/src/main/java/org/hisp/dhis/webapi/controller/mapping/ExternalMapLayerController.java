package org.hisp.dhis.webapi.controller.mapping;



import org.hisp.dhis.mapping.ExternalMapLayer;
import org.hisp.dhis.schema.descriptors.ExternalMapLayerSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Viet Nguyen <viet@dhis2.org>
 */
@Controller
@RequestMapping( value = ExternalMapLayerSchemaDescriptor.API_ENDPOINT )
public class ExternalMapLayerController
    extends AbstractCrudController<ExternalMapLayer>
{
}
