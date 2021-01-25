package org.hisp.dhis.commons.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.dataelement.*;

import java.util.Set;

/**
 * @author Lars Helge Overland
 */
public class GetCategoryOptionCombosAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private CategoryService categoryService;

    public void setCategoryService( CategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer categoryComboId;

    public void setCategoryComboId( Integer categoryComboId )
    {
        this.categoryComboId = categoryComboId;
    }
    
    private String categoryComboUid;

    public void setCategoryComboUid( String categoryComboUid )
    {
        this.categoryComboUid = categoryComboUid;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Set<CategoryOptionCombo> categoryOptionCombos;

    public Set<CategoryOptionCombo> getCategoryOptionCombos()
    {
        return categoryOptionCombos;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        if ( id != null )
        {
            DataElement dataElement = dataElementService.getDataElement( id );

            if ( dataElement != null )
            {
                categoryOptionCombos = dataElement.getCategoryOptionCombos();
            }
        }
        else if ( categoryComboId != null )
        {
            CategoryCombo categoryCombo = categoryService.getCategoryCombo( categoryComboId );
            
            if ( categoryCombo != null )
            {
                categoryOptionCombos = categoryCombo.getOptionCombos();
            }
        }
        else if ( categoryComboUid != null )
        {
            CategoryCombo categoryCombo = categoryService.getCategoryCombo( categoryComboUid );
            
            if ( categoryCombo != null )
            {
                categoryOptionCombos = categoryCombo.getOptionCombos();
            }
        }

        return SUCCESS;
    }
}
