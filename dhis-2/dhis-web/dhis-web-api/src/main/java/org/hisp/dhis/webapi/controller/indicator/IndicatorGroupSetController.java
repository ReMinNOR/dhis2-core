package org.hisp.dhis.webapi.controller.indicator;



import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.schema.descriptors.IndicatorGroupSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = IndicatorGroupSetSchemaDescriptor.API_ENDPOINT )
public class IndicatorGroupSetController
    extends AbstractCrudController<IndicatorGroupSet>
{
}
