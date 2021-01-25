package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.Category;
import org.hisp.dhis.schema.descriptors.CategorySchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategorySchemaDescriptor.API_ENDPOINT )
public class CategoryController
    extends AbstractCrudController<Category>
{
}
