package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.TreeStateManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class ExpandSubtreeAction
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

    private TreeStateManager treeStateManager;

    public void setTreeStateManager( TreeStateManager treeStateManager )
    {
        this.treeStateManager = treeStateManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String parentId;

    public void setParentId( String organisationUnitId )
    {
        this.parentId = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> parents = new ArrayList<>();

    public List<OrganisationUnit> getParents()
    {
        return parents;
    }

    private Map<OrganisationUnit, List<OrganisationUnit>> childrenMap = new HashMap<>();

    public Map<OrganisationUnit, List<OrganisationUnit>> getChildrenMap()
    {
        return childrenMap;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit parent = organisationUnitService.getOrganisationUnit( parentId );

        if ( parent == null )
        {
            throw new RuntimeException( "OrganisationUnit with id " + parentId + " doesn't exist" );
        }

        treeStateManager.setSubtreeExpanded( parent );

        addParentWithChildren( parent );

        return SUCCESS;
    }

    private void addParentWithChildren( OrganisationUnit parent )
        throws Exception
    {
        List<OrganisationUnit> children = parent.getSortedChildren();

        parents.add( parent );

        childrenMap.put( parent, children );

        for ( OrganisationUnit child : children )
        {
            if ( treeStateManager.isSubtreeExpanded( child ) )
            {
                addParentWithChildren( child );
            }
        }
    }
}
