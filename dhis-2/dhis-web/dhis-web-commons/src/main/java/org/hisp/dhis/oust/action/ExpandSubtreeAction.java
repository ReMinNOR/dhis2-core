package org.hisp.dhis.oust.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: ExpandSubtreeAction.java 2869 2007-02-20 14:26:09Z andegje $
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
            return INPUT;
        }

        addParentWithChildren( parent );

        return SUCCESS;
    }

    private void addParentWithChildren( OrganisationUnit parent )
        throws Exception
    {
        List<OrganisationUnit> children = parent.getSortedChildren();

        parents.add( parent );

        childrenMap.put( parent, children );
    }
}
