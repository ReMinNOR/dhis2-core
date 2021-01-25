package org.hisp.dhis.oust.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import java.util.Collection;
import java.util.HashSet;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: AddSelectedOrganisationUnitAction.java 2869 2007-02-20
 *          14:26:09Z andegje $
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
        selectedUnits = new HashSet<>( selectionTreeManager.getSelectedOrganisationUnits() );

        if ( id != null )
        {
            OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );
            selectedUnits.add( unit );
        }

        if ( level != null )
        {
            selectedUnits.addAll( organisationUnitService.getOrganisationUnitsAtLevel( level ) );
        }

        if ( organisationUnitGroupId != null )
        {
            selectedUnits.addAll( organisationUnitGroupService.getOrganisationUnitGroup( organisationUnitGroupId )
                .getMembers() );
        }

        if ( children != null && children == true )
        {
            for ( OrganisationUnit selected : selectionTreeManager.getSelectedOrganisationUnits() )
            {
                selectedUnits.addAll( organisationUnitService.getOrganisationUnitWithChildren( selected.getId() ) );
            }
        }

        selectionTreeManager.setSelectedOrganisationUnits( selectedUnits );

        return SUCCESS;
    }
}
