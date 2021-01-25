package org.hisp.dhis.ouwt.action;



import java.util.Collection;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;

import com.opensymphony.xwork2.Action;

/**
 * @author mortenoh
 */
public class GetSelectedOrganisationUnitsAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private Collection<OrganisationUnit> selectedUnits;

    public Collection<OrganisationUnit> getSelectedUnits()
    {
        return selectedUnits;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        selectedUnits = selectionManager.getSelectedOrganisationUnits();

        return SUCCESS;
    }
}
