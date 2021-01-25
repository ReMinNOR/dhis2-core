package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.paging.ActionPagingSupport;

public class GetSectionIndicatorsAction
    extends ActionPagingSupport<Indicator>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private List<Indicator> indicators = new ArrayList<>();

    public List<Indicator> getIndicators()
    {
        return indicators;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( dataSetId == null )
        {
            return SUCCESS;
        }
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        indicators = new ArrayList<>( dataSet.getIndicators() );
        
        for ( Section section : dataSet.getSections() )
        {
            indicators.removeAll( section.getIndicators() );
        }
        
        Collections.sort( indicators );
        
        return SUCCESS;
    }
}
