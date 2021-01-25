package org.hisp.dhis.webapi.controller.event;



import java.util.List;

import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.fieldfilter.Defaults;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.query.Order;
import org.hisp.dhis.query.Query;
import org.hisp.dhis.query.QueryParserException;
import org.hisp.dhis.schema.descriptors.ProgramSchemaDescriptor;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.controller.metadata.MetadataExportControllerUtils;
import org.hisp.dhis.webapi.webdomain.WebMetadata;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.google.common.collect.Lists;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Controller
@RequestMapping( value = ProgramSchemaDescriptor.API_ENDPOINT )
public class ProgramController
    extends AbstractCrudController<Program>
{
    @Autowired
    private ProgramService programService;

    @Override
    @SuppressWarnings( "unchecked" )
    protected List<Program> getEntityList( WebMetadata metadata, WebOptions options, List<String> filters, List<Order> orders )
        throws QueryParserException
    {
        boolean userFilter = Boolean.parseBoolean( options.getOptions().get( "userFilter" ) );

        List<Program> entityList;
        Query query = queryService.getQueryFromUrl( getEntityClass(), filters, orders, getPaginationData( options ), options.getRootJunction() );
        query.setDefaultOrder();
        query.setDefaults( Defaults.valueOf( options.get( "defaults", DEFAULTS ) ) );

        if ( options.getOptions().containsKey( "query" ) )
        {
            entityList = Lists.newArrayList( manager.filter( getEntityClass(), options.getOptions().get( "query" ) ) );
        }
        else
        {
            entityList = (List<Program>) queryService.query( query );
        }

        if ( userFilter )
        {
            List<Program> programs = programService.getUserPrograms();
            entityList.retainAll( programs );
            metadata.setPager( null );
        }

        return entityList;
    }

    @RequestMapping( value = "/{uid}/metadata", method = RequestMethod.GET )
    public ResponseEntity<RootNode> getProgramWithDependencies( @PathVariable( "uid" ) String pvUid, @RequestParam( required = false, defaultValue = "false" ) boolean download ) throws WebMessageException
    {
        Program program = programService.getProgram( pvUid );

        if ( program == null )
        {
            throw new WebMessageException( WebMessageUtils.notFound( "Program not found for uid: " + pvUid ) );
        }

        return MetadataExportControllerUtils.getWithDependencies( contextService, exportService, program, download );
    }
}
