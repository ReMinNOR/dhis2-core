package org.hisp.dhis.webapi.controller;



import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = "/i18n" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class I18nController
{
    private final I18nManager i18nManager;

    private final ObjectMapper jsonMapper;

    public I18nController(
        I18nManager i18nManager,
        ObjectMapper jsonMapper )
    {
        this.i18nManager = i18nManager;
        this.jsonMapper = jsonMapper;
    }

    @RequestMapping( method = RequestMethod.POST )
    public void postI18n( @RequestParam( value = "package", required = false, defaultValue = "org.hisp.dhis" ) String searchPackage,
        HttpServletResponse response, InputStream inputStream ) throws Exception
    {
        I18n i18n = i18nManager.getI18n( searchPackage );
        Map<String, String> output = new HashMap<>();

        List<String> input = jsonMapper.readValue( inputStream, new TypeReference<List<String>>()
        {
        } );

        for ( String key : input )
        {
            String value = i18n.getString( key );

            if ( value != null )
            {
                output.put( key, value );
            }
        }

        response.setContentType( MediaType.APPLICATION_JSON_VALUE );
        jsonMapper.writeValue( response.getOutputStream(), output );
    }
}
