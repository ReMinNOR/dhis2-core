package org.hisp.dhis.oust.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Torgeir Lorange Ostby
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

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
    }

    private Boolean children;

    public void setChildren( Boolean children )
    {
        this.children = children;
    }

    private Integer organisationUnitGroupId;

    public void setOrganisationUnitGroupId( Integer organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
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
        selectedUnits = selectionTreeManager.getSelectedOrganisationUnits();

        if ( id != null )
        {
            OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

            // TODO fix this, not pretty, but selectionTreeManager is not
            // correctly handling adding/removing selected orgunits
            while ( selectedUnits.remove( unit ) )
            {
            }
        }

        if ( level != null )
        {
            selectedUnits.removeAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );
        }

        if ( organisationUnitGroupId != null )
        {
            selectedUnits.removeAll( organisationUnitGroupService.getOrganisationUnitGroup( organisationUnitGroupId ).getMembers() );
        }

        if ( children != null && children == true )
        {
            Set<OrganisationUnit> selectedOrganisationUnits = new HashSet<>( selectedUnits );

            for ( OrganisationUnit selected : selectedOrganisationUnits )
            {
                OrganisationUnit parent = selected.getParent();

                if ( !selectedOrganisationUnits.contains( parent ) )
                {
                    selectedUnits
                        .removeAll( organisationUnitService.getOrganisationUnitWithChildren( selected.getId() ) );

                    selectedUnits.add( selected );
                }
            }
        }

        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
