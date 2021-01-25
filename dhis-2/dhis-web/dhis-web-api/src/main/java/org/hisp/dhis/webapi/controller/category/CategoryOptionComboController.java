package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.schema.descriptors.CategoryOptionComboSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategoryOptionComboSchemaDescriptor.API_ENDPOINT )
public class CategoryOptionComboController
    extends AbstractCrudController<CategoryOptionCombo>
{
}
