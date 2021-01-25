package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.system.filter.IndicatorGroupWithoutGroupSetFilter;
import org.hisp.dhis.commons.filter.FilterUtils;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author mortenoh
 */
public class GetIndicatorGroupsAction
    extends ActionPagingSupport<IndicatorGroup>
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

    public boolean filterNoGroupSet = false;

    public void setFilterNoGroupSet( boolean filterNoGroupSet )
    {
        this.filterNoGroupSet = filterNoGroupSet;
    }

    private List<IndicatorGroup> indicatorGroups;

    public List<IndicatorGroup> getIndicatorGroups()
    {
        return indicatorGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        indicatorGroups = new ArrayList<>( indicatorService.getAllIndicatorGroups() );

        if ( filterNoGroupSet )
        {
            FilterUtils.filter( indicatorGroups, new IndicatorGroupWithoutGroupSetFilter() );
        }

        if ( key != null )
        {
            indicatorGroups = IdentifiableObjectUtils.filterNameByKey( indicatorGroups, key, true );
        }

        Collections.sort( indicatorGroups );

        if ( usePaging )
        {
            this.paging = createPaging( indicatorGroups.size() );

            indicatorGroups = indicatorGroups.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
