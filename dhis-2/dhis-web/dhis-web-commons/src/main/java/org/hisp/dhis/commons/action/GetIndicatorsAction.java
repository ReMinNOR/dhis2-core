package org.hisp.dhis.commons.action;



import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.util.ContextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
public class GetIndicatorsAction
    extends ActionPagingSupport<Indicator>
{
    private final static int ALL = 0;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<Indicator> indicators;

    public List<Indicator> getIndicators()
    {
        return indicators;
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
            IndicatorGroup indicatorGroup = indicatorService.getIndicatorGroup( id );

            if ( indicatorGroup != null )
            {
                indicators = new ArrayList<>( indicatorGroup.getMembers() );
            }
        }
        else if ( dataSetId != null )
        {
            DataSet dataset = dataSetService.getDataSet( dataSetId );

            if ( dataset != null )
            {
                indicators = new ArrayList<>( dataset.getIndicators() );
            }
        }
        else
        {
            indicators = new ArrayList<>( indicatorService.getAllIndicators() );

            ContextUtils.clearIfNotModified( ServletActionContext.getRequest(), ServletActionContext.getResponse(), indicators );
        }

        if ( key != null )
        {
            indicators = IdentifiableObjectUtils.filterNameByKey( indicators, key, true );
        }

        if ( indicators == null )
        {
            indicators = new ArrayList<>();
        }

        Collections.sort( indicators );

        if ( usePaging )
        {
            this.paging = createPaging( indicators.size() );

            indicators = indicators.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
