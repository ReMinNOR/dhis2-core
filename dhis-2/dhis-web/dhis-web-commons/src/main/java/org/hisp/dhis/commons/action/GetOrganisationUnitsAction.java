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
public class GetOrganisationUnitsAction
    extends ActionPagingSupport<OrganisationUnit>
{
    private final static int ALL = 0;

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

    private Integer level;

    public void setLevel( Integer level )
    {
        this.level = level;
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
        if ( level == null || level == ALL )
        {
            organisationUnits = new ArrayList<>( organisationUnitService.getAllOrganisationUnits() );
        }
        else
        {
            organisationUnits = new ArrayList<>(
                organisationUnitService.getOrganisationUnitsAtLevel( level ) );
        }

        Collections.sort( organisationUnits );

        if ( usePaging )
        {
            this.paging = createPaging( organisationUnits.size() );

            organisationUnits = organisationUnits.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
