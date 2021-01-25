package org.hisp.dhis.webapi.controller.event;



import java.util.List;
import java.util.Map;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.common.PagerUtils;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.fieldfilter.FieldFilterParams;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.Preset;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.query.Query;
import org.hisp.dhis.query.QueryParserException;
import org.hisp.dhis.query.QueryService;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.schema.descriptors.ProgramDataElementDimensionItemSchemaDescriptor;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.utils.PaginationUtils;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = ProgramDataElementDimensionItemSchemaDescriptor.API_ENDPOINT )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class ProgramDataElementController
{
    private final QueryService queryService;
    private final FieldFilterService fieldFilterService;
    private final ContextService contextService;
    private final SchemaService schemaService;
    private final ProgramService programService;

    public ProgramDataElementController( QueryService queryService, FieldFilterService fieldFilterService,
        ContextService contextService, SchemaService schemaService, ProgramService programService )
    {
        this.queryService = queryService;
        this.fieldFilterService = fieldFilterService;
        this.contextService = contextService;
        this.schemaService = schemaService;
        this.programService = programService;
    }

    @GetMapping
    @SuppressWarnings( "unchecked" )
    public @ResponseBody RootNode getObjectList( @RequestParam Map<String, String> rpParameters, OrderParams orderParams )
        throws QueryParserException
    {
        Schema schema = schemaService.getDynamicSchema( ProgramDataElementDimensionItem.class );

        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
        List<String> filters = Lists.newArrayList( contextService.getParameterValues( "filter" ) );
        List<Order> orders = orderParams.getOrders( schema );

        if ( fields.isEmpty() )
        {
            fields.addAll( Preset.ALL.getFields() );
        }

        WebOptions options = new WebOptions( rpParameters );
        WebMetadata metadata = new WebMetadata();

        List<ProgramDataElementDimensionItem> programDataElements;
        Query query = queryService.getQueryFromUrl( ProgramDataElementDimensionItem.class, filters, orders,
            PaginationUtils.getPaginationData( options ), options.getRootJunction() );
        query.setDefaultOrder();

        if ( options.contains( "program" ) )
        {
            String programUid = options.get( "program" );
            programDataElements = programService.getGeneratedProgramDataElements( programUid );
            query.setObjects( programDataElements );
        }

        programDataElements = (List<ProgramDataElementDimensionItem>) queryService.query( query );

        Pager pager = metadata.getPager();

        if ( options.hasPaging() && pager == null )
        {
            pager = new Pager( options.getPage(), programDataElements.size(), options.getPageSize() );
            programDataElements = PagerUtils.pageCollection( programDataElements, pager );
        }

        RootNode rootNode = NodeUtils.createMetadata();

        if ( pager != null )
        {
            rootNode.addChild( NodeUtils.createPager( pager ) );
        }

        rootNode.addChild( fieldFilterService.toCollectionNode( ProgramDataElementDimensionItem.class,
            new FieldFilterParams( programDataElements, fields ) ) );

        return rootNode;
    }
}
