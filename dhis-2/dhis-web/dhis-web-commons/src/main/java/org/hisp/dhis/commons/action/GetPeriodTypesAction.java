package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

/**
 * @author Tran Thanh Tri
 */
public class GetPeriodTypesAction
    extends ActionPagingSupport<PeriodType>
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        periodTypes = new ArrayList<>( periodService.getAllPeriodTypes() );

        if ( usePaging )
        {
            this.paging = createPaging( periodTypes.size() );

            periodTypes = periodTypes.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
