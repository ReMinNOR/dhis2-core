package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.predictor.PredictorGroup;
import org.hisp.dhis.schema.descriptors.PredictorGroupSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Jim Grace
 */
@Controller
@RequestMapping( value = PredictorGroupSchemaDescriptor.API_ENDPOINT )
public class PredictorGroupController
    extends AbstractCrudController<PredictorGroup>
{
}
