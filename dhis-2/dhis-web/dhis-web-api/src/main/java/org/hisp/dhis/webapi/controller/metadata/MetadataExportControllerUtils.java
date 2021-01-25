package org.hisp.dhis.webapi.controller.metadata;

import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.dxf2.metadata.MetadataExportParams;
import org.hisp.dhis.dxf2.metadata.MetadataExportService;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.ContextService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nonnull;



/**
 * Utilities for metadata export controllers.
 *
 * @author Volker Schmidt
 */
public abstract class MetadataExportControllerUtils
{
    /**
     * Returns the response entity for metadata download with dependencies.
     *
     * @param contextService     the context service that is used to retrieve request parameters.
     * @param exportService      the export service that is used to export metadata with dependencies.
     * @param identifiableObject the identifiable object that should be exported with dependencies.
     * @param download           <code>true</code> if the data should be downloaded (as attachment),
     *                           <code>false</code> otherwise.
     * @return the response with the metadata.
     */
    @Nonnull
    public static ResponseEntity<RootNode> getWithDependencies( @Nonnull ContextService contextService, @Nonnull MetadataExportService exportService, @Nonnull IdentifiableObject identifiableObject, boolean download )
    {
        final MetadataExportParams exportParams = exportService.getParamsFromMap( contextService.getParameterValuesMap() );
        exportService.validate( exportParams );

        RootNode rootNode = exportService.getMetadataWithDependenciesAsNode( identifiableObject, exportParams );
        return createResponseEntity( rootNode, download );
    }

    /**
     * Creates the response entity for the root node. Optionally it can be specified that the data
     * should be downloaded.
     *
     * @param rootNode the root node for which the response entity should be created.
     * @param download <code>true</code> if the data should be downloaded (as attachment),
     *                 <code>false</code> otherwise.
     * @return the response with the metadata.
     */
    @Nonnull
    public static ResponseEntity<RootNode> createResponseEntity( @Nonnull RootNode rootNode, boolean download )
    {
        HttpHeaders headers = new HttpHeaders();
        if ( download )
        {
            // triggers that corresponding message converter adds also a file name with a correct extension
            headers.add( HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=metadata" );
        }
        return new ResponseEntity<>( rootNode, headers, HttpStatus.OK );
    }

    private MetadataExportControllerUtils()
    {
        super();
    }
}
