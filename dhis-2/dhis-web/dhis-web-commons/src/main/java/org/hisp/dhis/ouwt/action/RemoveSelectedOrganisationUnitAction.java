package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: RemoveSelectedOrganisationUnitAction.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class RemoveSelectedOrganisationUnitAction
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
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

        if ( unit == null )
        {
            throw new RuntimeException( "OrganisationUnit with id " + id + " doesn't exist" );
        }

        selectedUnits = selectionManager.getSelectedOrganisationUnits();
        selectedUnits.remove( unit );
        selectionManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
