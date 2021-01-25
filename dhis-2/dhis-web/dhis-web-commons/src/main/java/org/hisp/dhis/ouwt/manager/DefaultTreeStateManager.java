package org.hisp.dhis.ouwt.manager;



import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.util.SessionUtils;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: DefaultTreeStateManager.java 5282 2008-05-28 10:41:06Z larshelg $
 */
public class DefaultTreeStateManager
    implements TreeStateManager
{
    private static final String SESSION_KEY_TREE_STATE = "dhis-ouwt-tree-state";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Configuration
    // -------------------------------------------------------------------------

    private boolean collapseClosesAllSubtrees;

    public void setCollapseClosesAllSubtrees( boolean collapseClosesAllSubtrees )
    {
        this.collapseClosesAllSubtrees = collapseClosesAllSubtrees;
    }

    // -------------------------------------------------------------------------
    // TreeStateManager implementation
    // -------------------------------------------------------------------------

    @Override
    public void setSubtreeExpanded( OrganisationUnit unit )
    {
        getTreeState().add( unit.getId() );
    }

    @Override
    public Collection<OrganisationUnit> setSubtreeCollapsed( OrganisationUnit unit )
    {
        if ( collapseClosesAllSubtrees )
        {
            return closeAllSubtrees( unit );
        }
        else
        {
            getTreeState().remove( unit.getId() );

            Set<OrganisationUnit> collapsedUnits = new HashSet<>( 1 );
            collapsedUnits.add( unit );
            return collapsedUnits;
        }
    }

    @Override
    public boolean isSubtreeExpanded( OrganisationUnit unit )
    {
        return getTreeState().contains( unit.getId() );
    }

    @Override
    public void clearTreeState()
    {
        getTreeState().clear();
    }

    // -------------------------------------------------------------------------
    // Support methods
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> closeAllSubtrees( OrganisationUnit parentUnit )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnitWithChildren( parentUnit
            .getId() );

        Set<OrganisationUnit> collapsedUnits = new HashSet<>();

        Set<Long> treeState = getTreeState();

        for ( OrganisationUnit unit : units )
        {
            if ( treeState.contains( unit.getId() ) )
            {
                treeState.remove( unit.getId() );
                collapsedUnits.add( unit );
            }
        }

        return collapsedUnits;
    }

    @SuppressWarnings( "unchecked" )
    private Set<Long> getTreeState()
    {
        Set<Long> treeState = (Set<Long>) SessionUtils.getSessionVar( SESSION_KEY_TREE_STATE );

        if ( treeState == null )
        {
            treeState = new HashSet<>();

            SessionUtils.setSessionVar( SESSION_KEY_TREE_STATE, treeState );
        }

        return treeState;
    }
}
