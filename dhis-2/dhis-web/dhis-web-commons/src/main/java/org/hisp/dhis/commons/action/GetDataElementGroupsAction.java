package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author Tran Thanh Tri
 * @author mortenoh
 */
public class GetDataElementGroupsAction
    extends ActionPagingSupport<DataElementGroup>
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
    // Input & output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<DataElementGroup> dataElementGroups;

    public List<DataElementGroup> getDataElementGroups()
    {
        return dataElementGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        dataElementGroups = new ArrayList<>( dataElementService.getAllDataElementGroups() );

        if ( key != null )
        {
            dataElementGroups = IdentifiableObjectUtils.filterNameByKey( dataElementGroups, key, true );
        }

        Collections.sort( dataElementGroups );

        if ( usePaging )
        {
            this.paging = createPaging( dataElementGroups.size() );

            dataElementGroups = dataElementGroups.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
