package org.hisp.dhis.oust.action;



import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Dang Duy Hieu
 * @version $Id$
 * @since 2010-04-27
 */
public class ClearSelectedOrganisationUnitsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    @Override
    public String execute()
    {
        selectionTreeManager.clearSelectedOrganisationUnits();
        
        return SUCCESS;
    }
}
