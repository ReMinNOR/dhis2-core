package org.hisp.dhis.oust.action;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: GetExpandedTreeAction.java 6260 2008-11-11 15:58:43Z larshelg $
 */
public class GetExpandedTreeAction
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
        roots = new ArrayList<>( selectionTreeManager.getRootOrganisationUnits() );

        Collections.sort( roots );

        // ---------------------------------------------------------------------
        // Get the units that need to be expanded in order for the selected
        // organisation units to be visible
        // ---------------------------------------------------------------------

        selected = selectionTreeManager.getSelectedOrganisationUnits();

        Collection<OrganisationUnit> pathNodes = findPathNodes( roots, selected );

        // ---------------------------------------------------------------------
        // Get the children of the roots
        // ---------------------------------------------------------------------

        for ( OrganisationUnit root : roots )
        {
            if ( pathNodes.contains( root ) )
            {
                addParentWithChildren( root, pathNodes );
            }
        }

        return SUCCESS;
    }

    private void addParentWithChildren( OrganisationUnit parent, Collection<OrganisationUnit> pathNodes )
        throws Exception
    {
        List<OrganisationUnit> children = parent.getSortedChildren();

        parents.add( parent );

        childrenMap.put( parent, children );

        for ( OrganisationUnit child : children )
        {
            if ( pathNodes.contains( child ) )
            {
                addParentWithChildren( child, pathNodes );
            }
        }
    }

    private final Collection<OrganisationUnit> findPathNodes( Collection<OrganisationUnit> roots,
        Collection<OrganisationUnit> selected )
    {
        Set<OrganisationUnit> pathNodes = new HashSet<>();

        for ( OrganisationUnit unit : selected )
        {
            if ( roots.contains( unit ) ) // Is parent => done
            {
                continue;
            }

            OrganisationUnit tmp = unit.getParent(); // Start with parent

            while ( !roots.contains( tmp ) )
            {
                pathNodes.add( tmp ); // Add each parent
                
                if ( tmp != null && tmp.getParent() != null )
                {
                    tmp = tmp.getParent();
                }                
            }

            pathNodes.add( tmp ); // Add the root
        }

        return pathNodes;
    }
}
