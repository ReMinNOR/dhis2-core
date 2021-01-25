package org.hisp.dhis.de.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.category.CategoryOptionCombo;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.minmax.MinMaxDataElement;
import org.hisp.dhis.minmax.MinMaxDataElementService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

/**
 * @author Torgeir Lorange Ostby
 */
public class RemoveMinMaxLimitsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private MinMaxDataElementService minMaxDataElementService;

    public void setMinMaxDataElementService( MinMaxDataElementService minMaxDataElementService )
    {
        this.minMaxDataElementService = minMaxDataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

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

    private String organisationUnitId;

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private String dataElementId;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private String categoryOptionComboId;

    public void setCategoryOptionComboId( String categoryOptionComboId )
    {
        this.categoryOptionComboId = categoryOptionComboId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        CategoryOptionCombo optionCombo = categoryService.getCategoryOptionCombo( categoryOptionComboId );

        MinMaxDataElement minMaxDataElement = minMaxDataElementService.getMinMaxDataElement( organisationUnit,
            dataElement, optionCombo );

        if ( minMaxDataElement != null )
        {
            minMaxDataElementService.deleteMinMaxDataElement( minMaxDataElement );
        }

        return SUCCESS;
    }
}
