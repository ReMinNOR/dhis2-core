package org.hisp.dhis.ouwt.action;



import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitQueryParams;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Chau Thu Tran
 */
public class GetOrganisationUnitsByNameAction
    implements Action
{
    private static final int MAX = 14;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private CurrentUserService currentUserService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String term;

    public void setTerm( String term )
    {
        this.term = term;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> organisationUnits = new ArrayList<>();

    public List<OrganisationUnit> getOrganisationUnits()
    {
        return organisationUnits;
    }

    // -------------------------------------------------------------------------
    // Implementation Action
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        term = term.toLowerCase();

        User user = currentUserService.getCurrentUser();

        OrganisationUnitQueryParams params = new OrganisationUnitQueryParams();
        
        if ( user != null && user.hasOrganisationUnit() )
        {
            params.setParents( user.getOrganisationUnits() );
        }        
        
        params.setQuery( term );
        params.setMax( MAX );
        
        organisationUnits = organisationUnitService.getOrganisationUnitsByQuery( params );
        
        return SUCCESS;
    }
}
