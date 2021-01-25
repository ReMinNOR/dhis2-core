package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardItem;
import org.hisp.dhis.dashboard.DashboardItemShape;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.schema.descriptors.DashboardItemSchemaDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = DashboardItemSchemaDescriptor.API_ENDPOINT )
public class DashboardItemController
    extends AbstractCrudController<DashboardItem>
{
    //TODO this controller class is only needed for the pre 2.30 old dashboard app and should be removed

    @Autowired
    private DashboardService dashboardService;

    @RequestMapping( value = "/{uid}/shape/{shape}", method = RequestMethod.PUT )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void putDashboardItemShape( @PathVariable String uid, @PathVariable DashboardItemShape shape,
        HttpServletRequest request, HttpServletResponse response ) throws Exception
    {
        DashboardItem item = dashboardService.getDashboardItem( uid );

        if ( item == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "Dashboard item does not exist: " + uid ) );
        }

        Dashboard dashboard = dashboardService.getDashboardFromDashboardItem( item );

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), dashboard ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this dashboard." );
        }

        item.setShape( shape );

        dashboardService.updateDashboardItem( item );
    }
}
