package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.ouwt.manager.TreeStateManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 */
public class GetExpandedTreeAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManger )
    {
        this.selectionManager = selectionManger;
    }

    private TreeStateManager treeStateManager;

    public void setTreeStateManager( TreeStateManager treeStateManager )
    {
        this.treeStateManager = treeStateManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> roots;

    public List<OrganisationUnit> getRoots()
    {
        return roots;
    }

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

    private Collection<OrganisationUnit> selected;

    public Collection<OrganisationUnit> getSelected()
    {
        return selected;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get the roots
        // ---------------------------------------------------------------------

        roots = new ArrayList<>( selectionManager.getRootOrganisationUnits() );

        Collections.sort( roots );

        // ---------------------------------------------------------------------
        // Get the children of the roots
        // ---------------------------------------------------------------------

        for ( OrganisationUnit root : roots )
        {
            if ( treeStateManager.isSubtreeExpanded( root ) )
            {
                addParentWithChildren( root );
            }
        }

        // ---------------------------------------------------------------------
        // Get the selected units
        // ---------------------------------------------------------------------

        selected = selectionManager.getSelectedOrganisationUnits();

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
