package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.common.IdentifiableObjectUtils;

/**
 * @author Jan Henrik Overland
 */
public class GetOrganisationUnitGroupSetsAction
    extends ActionPagingSupport<OrganisationUnitGroupSet>
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
    // Input & Output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<OrganisationUnitGroupSet> organisationUnitGroupSets;

    public List<OrganisationUnitGroupSet> getOrganisationUnitGroupSets()
    {
        return organisationUnitGroupSets;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        organisationUnitGroupSets = new ArrayList<>(
            organisationUnitGroupService.getAllOrganisationUnitGroupSets() );

        if ( key != null )
        {
            organisationUnitGroupSets = IdentifiableObjectUtils.filterNameByKey( organisationUnitGroupSets, key, true );
        }

        Collections.sort( organisationUnitGroupSets );

        if ( usePaging )
        {
            this.paging = createPaging( organisationUnitGroupSets.size() );

            organisationUnitGroupSets = organisationUnitGroupSets.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }

}
