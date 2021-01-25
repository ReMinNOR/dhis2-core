package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.CategoryOption;
import org.hisp.dhis.schema.descriptors.CategoryOptionSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategoryOptionSchemaDescriptor.API_ENDPOINT )
public class CategoryOptionController extends AbstractCrudController<CategoryOption>
{
    
}
