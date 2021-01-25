package org.hisp.dhis.commons.action;



import org.hisp.dhis.category.Category;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.paging.ActionPagingSupport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mortenoh
 */
public class GetDataElementCategoriesAction
    extends ActionPagingSupport<Category>
{
    static enum CategoryType
    {
        DISAGGREGATION,
        ATTRIBUTE
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CategoryService dataElementCategoryService;

    public void setCategoryService( CategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Category> dataElementCategories;

    public List<Category> getDataElementCategories()
    {
        return dataElementCategories;
    }

    private CategoryType type;

    public void setType( CategoryType type )
    {
        this.type = type;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( type == null )
        {
            dataElementCategories = new ArrayList<>(
                dataElementCategoryService.getAllDataElementCategories() );
        }
        else if ( type.equals( CategoryType.ATTRIBUTE ) )
        {
            dataElementCategories = new ArrayList<>(
                dataElementCategoryService.getAttributeCategories() );
        }
        else if ( type.equals( CategoryType.DISAGGREGATION ) )
        {
            dataElementCategories = new ArrayList<>(
                dataElementCategoryService.getDisaggregationCategories() );
        }

        Collections.sort( dataElementCategories );

        if ( usePaging )
        {
            this.paging = createPaging( dataElementCategories.size() );

            dataElementCategories = dataElementCategories.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }

}
