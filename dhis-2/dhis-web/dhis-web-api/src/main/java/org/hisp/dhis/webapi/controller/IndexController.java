package org.hisp.dhis.webapi.controller;



import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.ComplexNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.node.types.SimpleNode;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
public class IndexController
{
    @Autowired
    private SchemaService schemaService;

    @Autowired
    private ContextService contextService;

    //--------------------------------------------------------------------------
    // GET
    //--------------------------------------------------------------------------

    @RequestMapping( value = "/api", method = RequestMethod.GET )
    public void getIndex( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String location = response.encodeRedirectURL( "/resources" );
        response.sendRedirect( ContextUtils.getRootPath( request ) + location );
    }

    @RequestMapping( value = "/", method = RequestMethod.GET )
    public void getIndexWithSlash( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        String location = response.encodeRedirectURL( "/resources" );
        response.sendRedirect( ContextUtils.getRootPath( request ) + location );
    }

    @RequestMapping( value = "/resources", method = RequestMethod.GET )
    public @ResponseBody RootNode getResources()
    {
        return createRootNode();
    }

    private RootNode createRootNode()
    {
        RootNode rootNode = NodeUtils.createMetadata();
        CollectionNode collectionNode = rootNode.addChild( new CollectionNode( "resources" ) );

        for ( Schema schema : schemaService.getSchemas() )
        {
            if ( schema.haveApiEndpoint() )
            {
                ComplexNode complexNode = collectionNode.addChild( new ComplexNode( "resource" ) );

                // TODO add i18n to this
                complexNode.addChild( new SimpleNode( "displayName", beautify( schema.getPlural() ) ) );
                complexNode.addChild( new SimpleNode( "singular", schema.getSingular() ) );
                complexNode.addChild( new SimpleNode( "plural", schema.getPlural() ) );
                complexNode.addChild( new SimpleNode( "href", contextService.getApiPath() + schema.getRelativeApiEndpoint() ) );
            }
        }

        return rootNode;
    }

    private String beautify( String name )
    {
        String[] camelCaseWords = StringUtils.capitalize( name ).split( "(?=[A-Z])" );
        return StringUtils.join( camelCaseWords, " " ).trim();
    }
}
