package org.hisp.dhis.webapi.controller.user;



import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.hibernate.exception.DeleteAccessDeniedException;
import org.hisp.dhis.hibernate.exception.UpdateAccessDeniedException;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.query.QueryParserException;
import org.hisp.dhis.schema.descriptors.UserRoleSchemaDescriptor;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = UserRoleSchemaDescriptor.API_ENDPOINT )
public class UserRoleController
    extends AbstractCrudController<UserAuthorityGroup>
{
    @Autowired
    private UserService userService;

    @Override
    protected List<UserAuthorityGroup> getEntityList( WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders )
        throws QueryParserException
    {
        List<UserAuthorityGroup> entityList = super.getEntityList( metadata, options, filters, orders );

        if ( options.getOptions().containsKey( "canIssue" ) && Boolean.parseBoolean( options.getOptions().get( "canIssue" ) ) )
        {
            userService.canIssueFilter( entityList );
        }

        return entityList;
    }

    @RequestMapping( value = "/{id}/users/{userId}", method = { RequestMethod.POST, RequestMethod.PUT } )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void addUserToRole( @PathVariable( value = "id" ) String pvId, @PathVariable( "userId" ) String pvUserId, HttpServletResponse response ) throws WebMessageException
    {
        UserAuthorityGroup userAuthorityGroup = userService.getUserAuthorityGroup( pvId );

        if ( userAuthorityGroup == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "UserRole does not exist: " + pvId ) );
        }

        User user = userService.getUser( pvUserId );

        if ( user == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "User does not exist: " + pvId ) );
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), userAuthorityGroup ) )
        {
            throw new UpdateAccessDeniedException( "You don't have the proper permissions to update this object." );
        }

        if ( !user.getUserCredentials().getUserAuthorityGroups().contains( userAuthorityGroup ) )
        {
            user.getUserCredentials().getUserAuthorityGroups().add( userAuthorityGroup );
            userService.updateUserCredentials( user.getUserCredentials() );
        }
    }

    @RequestMapping( value = "/{id}/users/{userId}", method = RequestMethod.DELETE )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void removeUserFromRole( @PathVariable( value = "id" ) String pvId, @PathVariable( "userId" ) String pvUserId, HttpServletResponse response ) throws WebMessageException
    {
        UserAuthorityGroup userAuthorityGroup = userService.getUserAuthorityGroup( pvId );

        if ( userAuthorityGroup == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "UserRole does not exist: " + pvId ) );
        }

        User user = userService.getUser( pvUserId );

        if ( user == null || user.getUserCredentials() == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "User does not exist: " + pvId ) );
        }

        if ( !aclService.canUpdate( currentUserService.getCurrentUser(), userAuthorityGroup ) )
        {
            throw new DeleteAccessDeniedException( "You don't have the proper permissions to delete this object." );
        }

        if ( user.getUserCredentials().getUserAuthorityGroups().contains( userAuthorityGroup ) )
        {
            user.getUserCredentials().getUserAuthorityGroups().remove( userAuthorityGroup );
            userService.updateUserCredentials( user.getUserCredentials() );
        }
    }
}
