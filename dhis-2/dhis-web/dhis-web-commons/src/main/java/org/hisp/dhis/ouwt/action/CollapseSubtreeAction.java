package org.hisp.dhis.ouwt.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.TreeStateManager;

import java.util.Collection;

/**
 * @author Torgeir Lorange Ostby
 */
public class CollapseSubtreeAction
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

    public void setParentId( String parentId )
    {
        this.parentId = parentId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> collapsedUnits;

    public Collection<OrganisationUnit> getCollapsedUnits()
    {
        return collapsedUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit parentUnit = organisationUnitService.getOrganisationUnit( parentId );

        collapsedUnits = treeStateManager.setSubtreeCollapsed( parentUnit );

        return SUCCESS;
    }
}
