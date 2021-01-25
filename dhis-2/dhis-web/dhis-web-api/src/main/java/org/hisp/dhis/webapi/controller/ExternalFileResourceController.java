package org.hisp.dhis.webapi.controller;


import org.hisp.dhis.feedback.Status;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.fileresource.ExternalFileResource;
import org.hisp.dhis.fileresource.ExternalFileResourceService;
import org.hisp.dhis.fileresource.FileResource;
import org.hisp.dhis.fileresource.FileResourceService;
import org.hisp.dhis.schema.descriptors.ExternalFileResourceSchemaDescriptor;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;

/**
 * @author Stian Sandvold
 */
@Controller
@RequestMapping( ExternalFileResourceSchemaDescriptor.API_ENDPOINT )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ExternalFileResourceController
{
    @Autowired
    private ExternalFileResourceService externalFileResourceService;

    @Autowired
    private FileResourceService fileResourceService;

    /**
     * Returns a file associated with the externalFileResource resolved from the accessToken.
     * <p>
     * Only files contained in externalFileResources with a valid accessToken, expiration date null or in the future
     * are files allowed to be served trough this endpoint.
     *
     * @param accessToken a unique string that resolves to a given externalFileResource
     * @param response
     * @throws WebMessageException
     */
    @RequestMapping( value = "/{accessToken}", method = RequestMethod.GET )
    public void getExternalFileResource( @PathVariable String accessToken,
        HttpServletResponse response )
        throws WebMessageException
    {
        ExternalFileResource externalFileResource = externalFileResourceService
            .getExternalFileResourceByAccessToken( accessToken );

        if ( externalFileResource == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "No file found with key '" + accessToken + "'" ) );
        }

        if ( externalFileResource.getExpires() != null && externalFileResource.getExpires().before( new Date() ) )
        {
            throw new WebMessageException( WebMessageUtils
                .createWebMessage( "The key you requested has expired", Status.WARNING, HttpStatus.GONE ) );
        }

        FileResource fileResource = externalFileResource.getFileResource();

        response.setContentType( fileResource.getContentType() );
        response.setContentLength( new Long( fileResource.getContentLength() ).intValue() );
        response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileResource.getName() );
        setNoStore( response );

        try
        {
            fileResourceService.copyFileResourceContent( fileResource, response.getOutputStream() );
        }
        catch ( IOException e )
        {
            throw new WebMessageException( WebMessageUtils.error( "Failed fetching the file from storage",
                "There was an exception when trying to fetch the file from the storage backend, could be network or filesystem related" ) );
        }
    }
}
