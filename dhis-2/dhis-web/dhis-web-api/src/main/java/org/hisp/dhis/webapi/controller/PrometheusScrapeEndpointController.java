package org.hisp.dhis.webapi.controller;



import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.exporter.common.TextFormat;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.io.Writer;

/**
 * @author Luciano Fiandesio
 */
@Profile("!test")
@Controller
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class PrometheusScrapeEndpointController
{
    private final CollectorRegistry collectorRegistry;

    public PrometheusScrapeEndpointController( CollectorRegistry collectorRegistry )
    {
        this.collectorRegistry = collectorRegistry;
    }

    @RequestMapping( value = "/metrics", method = RequestMethod.GET, produces = TextFormat.CONTENT_TYPE_004 )
    @ResponseBody
    public String scrape()
    {
        try
        {
            Writer writer = new StringWriter();
            TextFormat.write004( writer, this.collectorRegistry.metricFamilySamples() );
            return writer.toString();
        }
        catch ( IOException ex )
        {
            // This never happens since StringWriter::write() doesn't throw IOException

            throw new UncheckedIOException( "Writing metrics failed", ex );
        }
    }
}
