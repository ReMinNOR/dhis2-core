package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.schema.descriptors.ConstantSchemaDescriptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ConstantSchemaDescriptor.API_ENDPOINT )
public class ConstantController
    extends AbstractCrudController<Constant>
{
}
