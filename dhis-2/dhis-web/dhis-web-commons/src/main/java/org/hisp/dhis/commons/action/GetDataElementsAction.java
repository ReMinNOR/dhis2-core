package org.hisp.dhis.commons.action;



import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.category.CategoryCombo;
import org.hisp.dhis.category.CategoryService;
import org.hisp.dhis.dataelement.DataElementDomain;
import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.AggregatableDataElementFilter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class GetDataElementsAction
    extends ActionPagingSupport<DataElement>
{
    private final static int ALL = 0;

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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Input & output
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

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String periodTypeName;

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private boolean aggregate = false;

    public void setAggregate( boolean aggregate )
    {
        this.aggregate = aggregate;
    }

    public String domain;

    public void setDomain( String domain )
    {
        this.domain = domain;
    }

    private List<DataElement> dataElements;

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( id != null && id != ALL )
        {
            DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( id );

            if ( dataElementGroup != null )
            {
                dataElements = new ArrayList<>( dataElementGroup.getMembers() );
            }
        }
        else if ( categoryComboId != null && categoryComboId != ALL )
        {
            CategoryCombo categoryCombo = categoryService.getCategoryCombo( categoryComboId );

            if ( categoryCombo != null )
            {
                dataElements = new ArrayList<>(
                    dataElementService.getDataElementByCategoryCombo( categoryCombo ) );
            }
        }
        else if ( dataSetId != null )
        {
            DataSet dataset = dataSetService.getDataSet( dataSetId );

            if ( dataset != null )
            {
                dataElements = new ArrayList<>( dataset.getDataElements() );
            }
        }
        else if ( periodTypeName != null )
        {
            PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

            if ( periodType != null )
            {
                dataElements = new ArrayList<>( dataElementService.getDataElementsByPeriodType( periodType ) );
            }
        }
        else if ( domain != null )
        {
            if ( domain.equals( DataElementDomain.AGGREGATE.getValue() ) )
            {
                dataElements = new ArrayList<>(
                    dataElementService.getDataElementsByDomainType( DataElementDomain.AGGREGATE ) );
            }
            else
            {
                dataElements = new ArrayList<>(
                    dataElementService.getDataElementsByDomainType( DataElementDomain.TRACKER ) );
            }
        }
        else
        {
            dataElements = new ArrayList<>( dataElementService.getAllDataElements() );

            ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), dataElements );
        }

        if ( key != null )
        {
            dataElements = IdentifiableObjectUtils.filterNameByKey( dataElements, key, true );
        }

        if ( dataElements == null )
        {
            dataElements = new ArrayList<>();
        }

        Collections.sort( dataElements );

        if ( aggregate )
        {
            FilterUtils.filter( dataElements, new AggregatableDataElementFilter() );
        }

        if ( usePaging )
        {
            this.paging = createPaging( dataElements.size() );

            dataElements = dataElements.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
