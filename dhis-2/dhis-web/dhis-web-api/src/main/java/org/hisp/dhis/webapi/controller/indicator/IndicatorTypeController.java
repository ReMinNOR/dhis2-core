package org.hisp.dhis.webapi.controller.indicator;



import org.hisp.dhis.indicator.IndicatorType;
import org.hisp.dhis.schema.descriptors.IndicatorTypeSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = IndicatorTypeSchemaDescriptor.API_ENDPOINT )
public class IndicatorTypeController
    extends AbstractCrudController<IndicatorType>
{
}
