package org.hisp.dhis.webapi.controller.category;



import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.schema.descriptors.CategoryComboSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.controller.metadata.MetadataExportControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = CategoryComboSchemaDescriptor.API_ENDPOINT )
public class CategoryComboController
    extends AbstractCrudController<CategoryCombo>
{
    @Autowired
    private CategoryService categoryService;

    @RequestMapping( value = "/{uid}/metadata", method = RequestMethod.GET )
    public ResponseEntity<RootNode> getDataSetWithDependencies( @PathVariable( "uid" ) String pvUid, @RequestParam( required = false, defaultValue = "false" ) boolean download )
        throws WebMessageException, IOException
    {
        CategoryCombo categoryCombo = categoryService.getCategoryCombo( pvUid );

        if ( categoryCombo == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "CategoryCombo not found for uid: " + pvUid ) );
        }

        return MetadataExportControllerUtils.getWithDependencies( contextService, exportService, categoryCombo, download );
    }

    @Override
    public void postCreateEntity( CategoryCombo categoryCombo )
    {
        categoryService.updateOptionCombos( categoryCombo );
    }

    @Override
    public void postUpdateEntity( CategoryCombo categoryCombo )
    {
        categoryService.updateOptionCombos( categoryCombo );
    }
}
