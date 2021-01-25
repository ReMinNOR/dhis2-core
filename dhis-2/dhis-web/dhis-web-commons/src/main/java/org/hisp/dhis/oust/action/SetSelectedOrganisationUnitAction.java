package org.hisp.dhis.oust.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

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

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private String id;

    public void setId( String organisationUnitId )
    {
        this.id = organisationUnitId;
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
            selectionTreeManager.clearSelectedOrganisationUnits();
            return SUCCESS;
        }

        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

        if ( unit == null )
        {
            throw new RuntimeException( "OrganisationUnit with id " + id + " doesn't exist" );
        }

        selectedUnits = new HashSet<>( 1 );
        selectedUnits.add( unit );
        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
