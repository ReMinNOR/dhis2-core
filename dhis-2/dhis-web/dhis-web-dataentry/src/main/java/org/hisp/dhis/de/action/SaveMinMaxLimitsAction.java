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
public class SaveMinMaxLimitsAction
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

    private Integer minLimit;

    public void setMinLimit( Integer minLimit )
    {
        this.minLimit = minLimit;
    }

    private Integer maxLimit;

    public void setMaxLimit( Integer maxLimit )
    {
        this.maxLimit = maxLimit;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        minLimit = minLimit != null ? minLimit : 0;
        maxLimit = maxLimit != null ? maxLimit : 0;

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        DataElement dataElement = dataElementService.getDataElement( dataElementId );

        CategoryOptionCombo optionCombo = categoryService
            .getCategoryOptionCombo( categoryOptionComboId );

        MinMaxDataElement minMaxDataElement = minMaxDataElementService.getMinMaxDataElement( organisationUnit,
            dataElement, optionCombo );

        if ( minMaxDataElement == null )
        {
            minMaxDataElement = new MinMaxDataElement( dataElement, organisationUnit, optionCombo,
                minLimit, maxLimit, false );

            minMaxDataElementService.addMinMaxDataElement( minMaxDataElement );
        }
        else
        {
            minMaxDataElement.setMin( minLimit );
            minMaxDataElement.setMax( maxLimit );
            minMaxDataElement.setGenerated( false );

            minMaxDataElementService.updateMinMaxDataElement( minMaxDataElement );
        }

        return SUCCESS;
    }
}
