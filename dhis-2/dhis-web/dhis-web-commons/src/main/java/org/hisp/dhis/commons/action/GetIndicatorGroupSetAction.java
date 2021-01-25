package org.hisp.dhis.commons.action;



import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 */
public class GetIndicatorGroupSetAction
    implements Action
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

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private IndicatorGroupSet indicatorGroupSet;

    public IndicatorGroupSet getIndicatorGroupSet()
    {
        return indicatorGroupSet;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        if ( id != null )
        {
            indicatorGroupSet = indicatorService.getIndicatorGroupSet( id );
        }

        return SUCCESS;
    }
}
