package org.hisp.dhis.security.action;



import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: LoggedInAction.java 5649 2008-09-05 20:07:34Z larshelg $
 */
public class RestrictOrganisationUnitsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
	
    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        User user = currentUserService.getCurrentUser();
        
        if ( user != null )
        {
            // -----------------------------------------------------------------
            // Initialize ouwt and selection tree
            // -----------------------------------------------------------------

            Set<OrganisationUnit> dataCaptureOrgUnits = user.getOrganisationUnits();
            Set<OrganisationUnit> dataViewOrgUnits = user.getDataViewOrganisationUnits();

            if ( !dataCaptureOrgUnits.isEmpty() )
            {
                selectionManager.setRootOrganisationUnits( dataCaptureOrgUnits );
                selectionManager.setSelectedOrganisationUnits( dataCaptureOrgUnits );
            }
            else
            {
                selectionManager.resetRootOrganisationUnits();
                selectionManager.clearSelectedOrganisationUnits();
            }
            
            if ( !dataViewOrgUnits.isEmpty() )
            {                
                selectionTreeManager.setRootOrganisationUnits( dataViewOrgUnits );                
                selectionTreeManager.setSelectedOrganisationUnits( dataViewOrgUnits );
            }
            else
            {                
                selectionTreeManager.resetRootOrganisationUnits();                
                selectionTreeManager.clearSelectedOrganisationUnits();
            }
        }

        return SUCCESS;
    }
}
