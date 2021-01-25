package org.hisp.dhis.commons.action;



import org.hisp.dhis.dataelement.DataElementGroupSet;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Tran Thanh Tri
 */
public class GetDataElementGroupSetAction
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

    private DataElementGroupSet dataElementGroupSet;

    public DataElementGroupSet getDataElementGroupSet()
    {
        return dataElementGroupSet;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        if ( id != null )
        {
            dataElementGroupSet = dataElementService.getDataElementGroupSet( id );
        }

        return SUCCESS;
    }
}
