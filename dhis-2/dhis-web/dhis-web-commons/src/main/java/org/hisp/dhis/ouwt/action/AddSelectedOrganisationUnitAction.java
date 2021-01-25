package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import java.util.Collection;
import java.util.List;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: AddSelectedOrganisationUnitAction.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class AddSelectedOrganisationUnitAction
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

    private List<String> id;

    public void setId( List<String> id )
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
        selectedUnits = selectionManager.getSelectedOrganisationUnits();

        if ( id != null )
        {
            for ( String currentId : id )
            {
                OrganisationUnit unit = organisationUnitService.getOrganisationUnit( currentId );
    
                if ( unit == null )
                {
                    throw new RuntimeException( "OrganisationUnit with id " + id + " doesn't exist" );
                }
    
                selectedUnits.add( unit );
            }
        }

        selectionManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
