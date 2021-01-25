package org.hisp.dhis.webapi.controller.outlierdetection;



import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.hisp.dhis.webapi.utils.ContextUtils.CONTENT_TYPE_CSV;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.outlierdetection.OutlierDetectionQuery;
import org.hisp.dhis.outlierdetection.OutlierDetectionRequest;
import org.hisp.dhis.outlierdetection.OutlierDetectionService;
import org.hisp.dhis.outlierdetection.OutlierDetectionResponse;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * Outlier detection API controller.
 *
 * @author Lars Helge Overland
 */
@RestController
@AllArgsConstructor
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
@PreAuthorize( "hasRole('ALL') or hasRole('F_RUN_VALIDATION')" )
public class OutlierDetectionController
{
    private final OutlierDetectionService outlierService;

    private final ContextUtils contextUtils;

    @GetMapping( value = "/outlierDetection", produces = { APPLICATION_JSON_VALUE } )
    public OutlierDetectionResponse getOutliersJson( OutlierDetectionQuery query )
    {
        OutlierDetectionRequest request = outlierService.getFromQuery( query );

        return outlierService.getOutlierValues( request );
    }

    @GetMapping( value = "/outlierDetection", produces = { ContextUtils.CONTENT_TYPE_CSV } )
    public void getOutliersCsv( OutlierDetectionQuery query,
        HttpServletResponse response ) throws IOException
    {
        OutlierDetectionRequest request = outlierService.getFromQuery( query );

        contextUtils.configureResponse( response, CONTENT_TYPE_CSV, CacheStrategy.NO_CACHE, "outlierdata.csv", true );

        outlierService.getOutlierValuesAsCsv( request, response.getOutputStream() );
    }
}
