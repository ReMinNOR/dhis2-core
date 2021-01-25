package org.hisp.dhis.webapi.controller;



import org.hisp.dhis.dxf2.importsummary.ImportSummary;
import org.hisp.dhis.dxf2.metadata.feedback.ImportReport;
import org.hisp.dhis.dxf2.synch.AvailabilityStatus;
import org.hisp.dhis.dxf2.synch.SynchronizationManager;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_JSON;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = SynchronizationController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class SynchronizationController
{
    public static final String RESOURCE_PATH = "/synchronization";

    @Autowired
    private SynchronizationManager synchronizationManager;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RenderService renderService;

    @PreAuthorize( "hasRole('ALL') or hasRole('F_EXPORT_DATA')" )
    @RequestMapping( value = "/dataPush", method = RequestMethod.POST )
    public void execute( HttpServletResponse response )
        throws IOException
    {
        ImportSummary summary = synchronizationManager.executeDataValuePush();

        response.setContentType( CONTENT_TYPE_JSON );
        renderService.toJson( response.getOutputStream(), summary );
    }

    @PreAuthorize( "hasRole('ALL')" )
    @RequestMapping( value = "/metadataPull", method = RequestMethod.POST )
    public void importMetaData( @RequestBody String url, HttpServletResponse response )
        throws IOException
    {
        ImportReport importReport = synchronizationManager.executeMetadataPull( url );

        response.setContentType( CONTENT_TYPE_JSON );
        renderService.toJson( response.getOutputStream(), importReport );
    }

    @RequestMapping( value = "/availability", method = RequestMethod.GET, produces = "application/json" )
    public @ResponseBody AvailabilityStatus remoteServerAvailable()
    {
        return synchronizationManager.isRemoteServerAvailable();
    }

    @RequestMapping( value = "/metadataRepo", method = RequestMethod.GET, produces = "application/json" )
    public @ResponseBody String getMetadataRepoIndex()
    {
        return restTemplate.getForObject( SettingKey.METADATA_REPO_URL.getDefaultValue().toString(), String.class );
    }
}
