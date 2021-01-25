package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.CategoryOptionGroup;
import org.hisp.dhis.schema.descriptors.CategoryOptionGroupSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategoryOptionGroupSchemaDescriptor.API_ENDPOINT )
public class CategoryOptionGroupController
    extends AbstractCrudController<CategoryOptionGroup>
{
}
