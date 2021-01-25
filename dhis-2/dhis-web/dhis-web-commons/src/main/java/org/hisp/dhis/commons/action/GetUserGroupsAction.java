package org.hisp.dhis.commons.action;



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.IdentifiableObjectUtils;
import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;

/*
 * @author mortenoh
 */
public class GetUserGroupsAction
    extends ActionPagingSupport<UserGroup>
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private String key;

    public void setKey( String key )
    {
        this.key = key;
    }

    private List<UserGroup> userGroups;

    public List<UserGroup> getUserGroups()
    {
        return userGroups;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        userGroups = new ArrayList<>( userGroupService.getAllUserGroups() );

        if ( key != null )
        {
            userGroups = IdentifiableObjectUtils.filterNameByKey( userGroups, key, true );
        }

        Collections.sort( userGroups );

        if ( usePaging )
        {
            this.paging = createPaging( userGroups.size() );

            userGroups = userGroups.subList( paging.getStartPos(), paging.getEndPos() );
        }

        return SUCCESS;
    }
}
