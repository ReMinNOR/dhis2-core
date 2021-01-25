package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author Tran Thanh Tri
 * @author mortenoh
 */
public class GetOrganisationUnitGroupsAction
    extends ActionPagingSupport<OrganisationUnitGroup>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<OrganisationUnitGroup> organisationUnitGroups;

    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        organisationUnitGroups = new ArrayList<>(
            organisationUnitGroupService.getAllOrganisationUnitGroups() );

        if ( key != null )
        {
            organisationUnitGroups = IdentifiableObjectUtils.filterNameByKey( organisationUnitGroups, key, true );
        }

        Collections.sort( organisationUnitGroups );

        if ( usePaging )
        {
            this.paging = createPaging( organisationUnitGroups.size() );

            organisationUnitGroups = organisationUnitGroups.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
