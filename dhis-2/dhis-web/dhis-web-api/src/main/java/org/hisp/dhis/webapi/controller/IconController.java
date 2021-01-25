package org.hisp.dhis.webapi.controller;



import com.google.common.net.MediaType;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.commons.util.StreamUtils;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.icon.Icon;
import org.hisp.dhis.icon.IconData;
import org.hisp.dhis.icon.IconService;
import org.hisp.dhis.schema.descriptors.IconSchemaDescriptor;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Kristian Wærstad
 */
@Controller
@RequestMapping( value = IconSchemaDescriptor.API_ENDPOINT )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class IconController
{
    private static final int TTL = 365;

    @Autowired
    private IconService iconService;

    @Autowired
    private ContextService contextService;

    @RequestMapping( method = RequestMethod.GET )
    public @ResponseBody
    List<IconData> getIcons( HttpServletResponse response, @RequestParam( required = false ) Collection<String> keywords )
    {
        Collection<IconData> icons;

        if ( keywords == null )
        {
            icons = iconService.getIcons();
        }
        else
        {
            icons = iconService.getIcons( keywords );
        }

        return icons.stream()
            .map( data -> data.setReference( String.format( "%s%s/%s/icon.%s", contextService.getApiPath(), IconSchemaDescriptor.API_ENDPOINT, data.getKey(), Icon.SUFFIX ) ) )
            .collect( Collectors.toList() );
    }

    @RequestMapping( value="/keywords", method = RequestMethod.GET )
    public @ResponseBody
    Collection<String> getKeywords( HttpServletResponse response )
    {
        return iconService.getKeywords();
    }

    @RequestMapping( value="/{iconKey}", method = RequestMethod.GET )
    public @ResponseBody
    IconData getIcon( HttpServletResponse response, @PathVariable String iconKey ) throws WebMessageException
    {
        Optional<IconData> icon = iconService.getIcon( iconKey );

        if ( !icon.isPresent() )
        {
            throw new WebMessageException( WebMessageUtils.notFound( String.format( "Icon not found: '%s", iconKey ) ) );
        }

        icon.get().setReference( String.format( "%s%s/%s/icon.%s", contextService.getApiPath(), IconSchemaDescriptor.API_ENDPOINT, icon.get().getKey(), Icon.SUFFIX ) );

        return icon.get();
    }

    @RequestMapping( value="/{iconKey}/icon.svg", method = RequestMethod.GET )
    public void getIconData( HttpServletResponse response, @PathVariable String iconKey ) throws WebMessageException,
        IOException
    {
        Optional<Resource> icon = iconService.getIconResource( iconKey );

        if ( !icon.isPresent() )
        {
            throw new WebMessageException( WebMessageUtils.notFound( String.format( "Icon resource not found: '%s", iconKey ) ) );
        }

        response.setHeader( "Cache-Control", CacheControl.maxAge( TTL, TimeUnit.DAYS ).getHeaderValue() );
        response.setContentType( MediaType.SVG_UTF_8.toString() );

        StreamUtils.copyThenCloseInputStream( icon.get().getInputStream(), response.getOutputStream() );
    }
}
