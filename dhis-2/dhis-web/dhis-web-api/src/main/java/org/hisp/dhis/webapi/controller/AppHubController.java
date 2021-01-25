package org.hisp.dhis.webapi.controller;



import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hisp.dhis.apphub.AppHubService;
import org.hisp.dhis.apphub.WebApp;

import org.hisp.dhis.appmanager.AppStatus;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Zubair Asghar
 */
@RestController
@RequestMapping( AppHubController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class AppHubController
{
    public static final String RESOURCE_PATH = "/appHub";

    @Autowired
    private AppHubService appHubService;

    @Autowired
    private I18nManager i18nManager;

    /**
     * Deprecated as of version 2.35 and should be removed eventually.
     */
    @GetMapping( produces = "application/json" )
    public  List<WebApp> listAppHub()
        throws IOException
    {
        return appHubService.getAppHub();
    }

    @GetMapping( value = "/{apiVersion}/**", produces = "application/json" )
    public String getAppHubApiResponse(
        @PathVariable String apiVersion, HttpServletRequest request )
    {
        String query = ContextUtils.getWildcardPathValue( request );

        return appHubService.getAppHubApiResponse( apiVersion, query );
    }

    @PostMapping( value = "/{versionId}" )
    @PreAuthorize( "hasRole('ALL') or hasRole('M_dhis-web-maintenance-appmanager')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void installAppFromAppHub( @PathVariable String versionId )
        throws WebMessageException
    {
        AppStatus status = appHubService.installAppFromAppHub( versionId );

        if ( !status.ok() )
        {
            String message = i18nManager.getI18n().getString( status.getMessage() );

            throw new WebMessageException( WebMessageUtils.conflict( message ) );
        }
    }
}
