package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dbms.DbmsManager;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = InternalServiceController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class InternalServiceController
{
    public static final String RESOURCE_PATH = "/internalServices";

    @Autowired
    private DhisConfigurationProvider config;

    @Autowired
    private DbmsManager dbmsManager;

    @Autowired
    private CurrentUserService currentUserService;

    @RequestMapping( value = "/emptyDatabase", method = RequestMethod.POST )
    @PreAuthorize( "hasRole('ALL')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void emptyDatabase()
    {
        boolean hasAllAuth = currentUserService.getCurrentUser().isAuthorized( UserAuthorityGroup.AUTHORITY_ALL );

        if ( config.isEnabled( ConfigurationKey.SYSTEM_INTERNAL_SERVICE_API ) && hasAllAuth )
        {
            dbmsManager.emptyDatabase();
        }
    }
}
