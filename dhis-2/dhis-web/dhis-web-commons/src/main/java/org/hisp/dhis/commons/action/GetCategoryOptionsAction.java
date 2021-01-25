package org.hisp.dhis.commons.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.category.CategoryOption;
import org.hisp.dhis.category.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class GetCategoryOptionsAction
    implements Action
{
    @Autowired
    private CategoryService categoryService;

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<CategoryOption> categoryOptions;

    public List<CategoryOption> getCategoryOptions()
    {
        return categoryOptions;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    @Override
    public String execute()
    {
        categoryOptions = new ArrayList<>( categoryService.getAllCategoryOptions() );
        
        Collections.sort( categoryOptions );
        
        return SUCCESS;
    }
}
