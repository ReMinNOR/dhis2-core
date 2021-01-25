package org.hisp.dhis.webapi.controller.mapping;



import com.google.common.collect.Lists;
import org.hisp.dhis.common.cache.CacheStrategy;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.fieldfilter.Defaults;
import org.hisp.dhis.mapgeneration.MapGenerationService;
import org.hisp.dhis.mapping.MapView;
import org.hisp.dhis.mapping.MappingService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.query.Query;
import org.hisp.dhis.query.QueryParserException;
import org.hisp.dhis.schema.descriptors.MapViewSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.utils.ContextUtils;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = MapViewSchemaDescriptor.API_ENDPOINT )
public class MapViewController
    extends AbstractCrudController<MapView>
{
    @Autowired
    private MappingService mappingService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private MapGenerationService mapGenerationService;

    @Autowired
    private ContextUtils contextUtils;

    //--------------------------------------------------------------------------
    // Get data
    //--------------------------------------------------------------------------

    @RequestMapping( value = { "/{uid}/data", "/{uid}/data.png" }, method = RequestMethod.GET )
    public void getMapViewData( @PathVariable String uid, HttpServletResponse response ) throws Exception
    {
        MapView mapView = mappingService.getMapView( uid );

        if ( mapView == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "Map view does not exist: " + uid ) );
        }

        renderMapViewPng( mapView, response );
    }

    @RequestMapping( value = { "/data", "/data.png" }, method = RequestMethod.GET )
    public void getMapView( Model model,
        @RequestParam( value = "in" ) String indicatorUid,
        @RequestParam( value = "ou" ) String organisationUnitUid,
        @RequestParam( value = "level", required = false ) Integer level,
        HttpServletResponse response ) throws Exception
    {
        if ( level == null )
        {
            OrganisationUnit unit = organisationUnitService.getOrganisationUnit( organisationUnitUid );
            level = unit.getLevel();
            level++;
        }

        MapView mapView = mappingService.getIndicatorLastYearMapView( indicatorUid, organisationUnitUid, level );

        renderMapViewPng( mapView, response );
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    protected List<MapView> getEntityList( WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders )
        throws QueryParserException
    {
        List<MapView> entityList;
        Query query = queryService.getQueryFromUrl( getEntityClass(), filters, orders, getPaginationData( options ), options.getRootJunction() );
        query.setDefaultOrder();
        query.setDefaults( Defaults.valueOf( options.get( "defaults", DEFAULTS ) ) );

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else
        {
            entityList = (List<MapView>) queryService.query( query );
        }

        return entityList;
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void renderMapViewPng( MapView mapView, HttpServletResponse response )
        throws Exception
    {
        BufferedImage image = mapGenerationService.generateMapImage( mapView );

        if ( image != null )
        {
            contextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PNG, CacheStrategy.RESPECT_SYSTEM_SETTING, "mapview.png", false );

            ImageIO.write( image, "PNG", response.getOutputStream() );
        }
        else
        {
            response.setStatus( HttpServletResponse.SC_NO_CONTENT );
        }
    }
}
