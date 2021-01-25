package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author mortenoh
 */
public class GetIndicatorGroupSetsAction
    extends ActionPagingSupport<IndicatorGroupSet>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<IndicatorGroupSet> indicatorGroupSets;

    public List<IndicatorGroupSet> getIndicatorGroupSets()
    {
        return indicatorGroupSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        indicatorGroupSets = new ArrayList<>( indicatorService.getAllIndicatorGroupSets() );

        if ( key != null )
        {
            indicatorGroupSets = IdentifiableObjectUtils.filterNameByKey( indicatorGroupSets, key, true );
        }

        Collections.sort( indicatorGroupSets );

        if ( usePaging )
        {
            this.paging = createPaging( indicatorGroupSets.size() );

            indicatorGroupSets = indicatorGroupSets.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
