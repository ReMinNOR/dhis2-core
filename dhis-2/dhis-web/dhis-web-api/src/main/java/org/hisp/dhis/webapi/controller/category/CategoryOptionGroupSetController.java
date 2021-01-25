package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.CategoryOptionGroupSet;
import org.hisp.dhis.schema.descriptors.CategoryOptionGroupSetSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategoryOptionGroupSetSchemaDescriptor.API_ENDPOINT )
public class CategoryOptionGroupSetController
    extends AbstractCrudController<CategoryOptionGroupSet>
{
}
