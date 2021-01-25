package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Torgeir Lorange Ostby
 */
public class SetSelectedOrganisationUnitAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    private Collection<OrganisationUnit> selectedUnits;

    public Collection<OrganisationUnit> getSelectedUnits()
    {
        return selectedUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( id == null )
        {
            selectionManager.clearSelectedOrganisationUnits();
            return SUCCESS;
        }

        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

        if ( unit != null )
        {
            selectedUnits = new HashSet<>( 1 );
            selectedUnits.add( unit );
    
            selectionManager.setSelectedOrganisationUnits( selectedUnits );
        }
        
        return SUCCESS;
    }
}
