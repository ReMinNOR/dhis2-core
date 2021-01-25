package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping( value = MenuController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class MenuController
{
    public static final String RESOURCE_PATH = "/menu";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserService userService;

    @Autowired
    private RenderService renderService;

    @ResponseStatus( HttpStatus.NO_CONTENT )
    @SuppressWarnings( "unchecked" )
    @RequestMapping( method = RequestMethod.POST, consumes = "application/json" )
    public void saveMenuOrder( InputStream input )
        throws Exception
    {
        List<String> apps = renderService.fromJson( input, List.class );

        User user = currentUserService.getCurrentUser();

        if ( apps != null && !apps.isEmpty() && user != null )
        {
            user.getApps().clear();
            user.getApps().addAll( apps );

            userService.updateUser( user );
        }
    }
}
