package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author mortenoh
 */
public class GetDataElementGroupSetsAction
    extends ActionPagingSupport<DataElementGroupSet>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<DataElementGroupSet> dataElementGroupSets;

    public List<DataElementGroupSet> getDataElementGroupSets()
    {
        return dataElementGroupSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        dataElementGroupSets = new ArrayList<>( dataElementService.getAllDataElementGroupSets() );

        if ( key != null )
        {
            dataElementGroupSets = IdentifiableObjectUtils.filterNameByKey( dataElementGroupSets, key, true );
        }

        Collections.sort( dataElementGroupSets );

        if ( usePaging )
        {
            this.paging = createPaging( dataElementGroupSets.size() );

            dataElementGroupSets = dataElementGroupSets.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
