package org.hisp.dhis.commons.action;



import org.hisp.dhis.dataelement.DataElementGroup;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class GetDataElementGroupAction
    implements Action
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

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private DataElementGroup dataElementGroup;

    public DataElementGroup getDataElementGroup()
    {
        return dataElementGroup;
    }

    private int memberCount;

    public int getMemberCount()
    {
        return memberCount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        if ( id != null )
        {
            dataElementGroup = dataElementService.getDataElementGroup( id );
            memberCount = dataElementGroup.getMembers().size();
        }

        return SUCCESS;
    }
}
