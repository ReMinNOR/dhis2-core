package org.hisp.dhis.webapi.controller.indicator;



import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.schema.descriptors.IndicatorGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = IndicatorGroupSchemaDescriptor.API_ENDPOINT )
public class IndicatorGroupController
    extends AbstractCrudController<IndicatorGroup>
{
}
