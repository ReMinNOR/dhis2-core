package org.hisp.dhis.ouwt.interceptor;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.interceptor.AbstractPreResultListener;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.ouwt.manager.TreeStateManager;

import com.opensymphony.xwork2.ActionInvocation;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: OrganisationUnitTreeInterceptor.java 1677 2006-06-13 21:58:54Z
 *          torgeilo $
 */
public class OrganisationUnitTreeInterceptor
    extends AbstractPreResultListener
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 8494211825850245931L;

    private static final String VALUE_KEY = "organisationUnitTree";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    private TreeStateManager treeStateManager;

    public void setTreeStateManager( TreeStateManager treeStateManager )
    {
        this.treeStateManager = treeStateManager;
    }

    // -------------------------------------------------------------------------
    // PreResult implementation
    // -------------------------------------------------------------------------

    @Override
    public void executeBeforeResult( ActionInvocation actionInvocation, String result )
        throws Exception
    {
        Collection<OrganisationUnit> selectedUnits;
        List<OrganisationUnit> rootUnits;

        selectedUnits = selectionManager.getSelectedOrganisationUnits();   
        
        rootUnits = new ArrayList<>( selectionManager.getRootOrganisationUnits() );

        if ( selectedUnits == null )
        {
            selectedUnits = Collections.emptySet();
        }

        List<TreeNode> rootNodes = new ArrayList<>( rootUnits.size() );

        Set<OrganisationUnit> pathUnits = getPathUnits( rootUnits, selectedUnits );

        rootNodes = createLevelNodes( pathUnits, rootUnits, selectedUnits );

        Map<String, Collection<TreeNode>> valueMap = new HashMap<>( 1 );
        valueMap.put( VALUE_KEY, rootNodes );
        actionInvocation.getStack().push( valueMap );
    }

    // -------------------------------------------------------------------------
    // Tree creation methods
    // -------------------------------------------------------------------------

    private List<TreeNode> createLevelNodes( Set<OrganisationUnit> pathUnits, Collection<OrganisationUnit> childUnits,
        Collection<OrganisationUnit> selectedUnits )
        throws Exception
    {
        List<OrganisationUnit> childUnitsSorted = new ArrayList<>( childUnits );
        Collections.sort( childUnitsSorted );

        List<TreeNode> childNodes = new ArrayList<>();

        for ( OrganisationUnit childUnit : childUnitsSorted )
        {
            TreeNode childNode = new TreeNode();
            childNode.setId( childUnit.getId() );
            childNode.setName( childUnit.getName() );
            childNode.setSelected( selectedUnits.contains( childUnit ) );
            childNode.setHasChildren( childUnit.getChildren().size() > 0 );

            if ( pathUnits.contains( childUnit ) || treeStateManager.isSubtreeExpanded( childUnit ) )
            {
                childNode.setChildren( createLevelNodes( pathUnits, childUnit.getChildren(), selectedUnits ) );
            }

            childNodes.add( childNode );
        }

        return childNodes;
    }

    private Set<OrganisationUnit> getPathUnits( Collection<OrganisationUnit> rootUnits,
        Collection<OrganisationUnit> childUnits )
    {
        // Leaf nodes not included.

        Set<OrganisationUnit> pathUnits = new HashSet<>();

        for ( OrganisationUnit childUnit : childUnits )
        {
            OrganisationUnit parentUnit = childUnit.getParent();

            while ( parentUnit != null && !rootUnits.contains( parentUnit ) )
            {
                pathUnits.add( parentUnit );
                parentUnit = parentUnit.getParent();
            }

            pathUnits.add( parentUnit );
        }

        return pathUnits;
    }
}
