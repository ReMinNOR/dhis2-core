package org.hisp.dhis.oust.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class GetUserCountAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private SelectionTreeManager selectionTreeManager;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private int userCount = 0;

    public int getUserCount()
    {
        return userCount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute() throws Exception
    {
        for ( OrganisationUnit organisationUnit : selectionTreeManager.getReloadedSelectedOrganisationUnits() )
        {
            userCount += organisationUnit.getUsers().size();
        }

        return SUCCESS;
    }
}
