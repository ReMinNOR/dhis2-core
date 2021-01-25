package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.paging.ActionPagingSupport;

/**
 * @author Lars Helge Overland
 */
public class GetOrganisationUnitChildrenAction
    extends ActionPagingSupport<OrganisationUnit>
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

    private Integer id;

    public void setId( Integer organisationUnitId )
    {
        this.id = organisationUnitId;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> organisationUnits;

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        OrganisationUnit unit = organisationUnitService.getOrganisationUnit( id );

        organisationUnits = new ArrayList<>( unit.getChildren() );

        Collections.sort( organisationUnits );

        if ( usePaging )
        {
            this.paging = createPaging( organisationUnits.size() );

            organisationUnits = organisationUnits.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
