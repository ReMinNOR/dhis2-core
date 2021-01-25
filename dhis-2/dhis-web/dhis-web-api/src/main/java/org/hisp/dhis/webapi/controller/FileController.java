package org.hisp.dhis.webapi.controller;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.setting.SettingKey;
import org.hisp.dhis.setting.SystemSettingManager;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = FileController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class FileController
{
    public static final String RESOURCE_PATH = "/files";

    @Autowired
    private SystemSettingManager systemSettingManager;

    @Autowired
    private ContextUtils contextUtils;

    @Autowired
    private WebMessageService webMessageService;

    // -------------------------------------------------------------------------
    // Custom script
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/script", method = RequestMethod.GET )
    public void getCustomScript( HttpServletResponse response, Writer writer )
        throws IOException
    {
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_JAVASCRIPT, CacheStrategy.CACHE_TWO_WEEKS );

        String content = (String) systemSettingManager.getSystemSetting( SettingKey.CUSTOM_JS, StringUtils.EMPTY );

        writer.write( content );
    }

    @RequestMapping( value = "/script", method = RequestMethod.POST, consumes = "application/javascript" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INSERT_CUSTOM_JS_CSS')" )
    public void postCustomScript( @RequestBody String content, HttpServletResponse response, HttpServletRequest request )
    {
        if ( content != null )
        {
            systemSettingManager.saveSystemSetting( SettingKey.CUSTOM_JS, content );
            webMessageService.send( WebMessageUtils.ok( "Custom script created" ), response, request );
        }
    }

    @RequestMapping( value = "/script", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INSERT_CUSTOM_JS_CSS')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void removeCustomScript( HttpServletResponse response )
    {
        systemSettingManager.deleteSystemSetting( SettingKey.CUSTOM_JS );
    }

    // -------------------------------------------------------------------------
    // Custom style
    // -------------------------------------------------------------------------

    /**
     * The style/external mapping enables style to be reached from login page / before authentication.
     */
    @RequestMapping( value = { "/style", "/style/external" }, method = RequestMethod.GET )
    public void getCustomStyle( HttpServletResponse response, Writer writer )
        throws IOException
    {
        contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSS, CacheStrategy.CACHE_TWO_WEEKS );

        String content = (String) systemSettingManager.getSystemSetting( SettingKey.CUSTOM_CSS, StringUtils.EMPTY );

        writer.write( content );
    }

    @RequestMapping( value = "/style", method = RequestMethod.POST, consumes = "text/css" )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INSERT_CUSTOM_JS_CSS')" )
    public void postCustomStyle( @RequestBody String content, HttpServletResponse response, HttpServletRequest request )
    {
        if ( content != null )
        {
            systemSettingManager.saveSystemSetting( SettingKey.CUSTOM_CSS, content );
            webMessageService.send( WebMessageUtils.ok( "Custom style created" ), response, request );
        }
    }

    @RequestMapping( value = "/style", method = RequestMethod.DELETE )
    @PreAuthorize( "hasRole('ALL') or hasRole('F_INSERT_CUSTOM_JS_CSS')" )
    @ResponseStatus( HttpStatus.NO_CONTENT )
    public void removeCustomStyle( HttpServletResponse response )
    {
        systemSettingManager.deleteSystemSetting( SettingKey.CUSTOM_CSS );
    }
}
