package org.hisp.dhis.webapi.controller;



import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Lars Helge Overland
 */
@Controller
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ReportTemplateController
{
    @Autowired
    private ContextUtils contextUtils;

    @RequestMapping( value = "/reportTemplate.xml", method = RequestMethod.GET, produces = "application/xml" )
    public void getReportDesignJrxml( HttpServletResponse response ) throws Exception
    {
        serveTemplate( response, ContextUtils.CONTENT_TYPE_XML, "jasper-report-template.jrxml" );
    }

    @RequestMapping( value = "/reportTemplate.html", method = RequestMethod.GET, produces = "application/xml" )
    public void getReportDesignHtml( HttpServletResponse response ) throws Exception
    {
        serveTemplate( response, ContextUtils.CONTENT_TYPE_HTML, "html-report-template.html" );
    }

    private void serveTemplate( HttpServletResponse response, String contentType, String template ) throws IOException
    {
        contextUtils.configureResponse( response, contentType, CacheStrategy.CACHE_1_HOUR, template, true );

        String content = IOUtils.toString( new ClassPathResource( template ).getInputStream(), StandardCharsets.UTF_8 );

        IOUtils.write( content, response.getWriter() );
    }
}
