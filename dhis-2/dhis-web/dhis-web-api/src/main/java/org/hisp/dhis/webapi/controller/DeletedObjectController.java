package org.hisp.dhis.webapi.controller;



import com.google.common.collect.Lists;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.deletedobject.DeletedObject;
import org.hisp.dhis.deletedobject.DeletedObjectQuery;
import org.hisp.dhis.deletedobject.DeletedObjectService;
import org.hisp.dhis.fieldfilter.FieldFilterParams;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.NodeUtils;
import org.hisp.dhis.node.Preset;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@RestController
@RequestMapping( value = "/deletedObjects" )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class DeletedObjectController
{
    private final FieldFilterService fieldFilterService;
    private final DeletedObjectService deletedObjectService;
    private final ContextService contextService;

    public DeletedObjectController( FieldFilterService fieldFilterService, DeletedObjectService deletedObjectService,
        ContextService contextService )
    {
        this.fieldFilterService = fieldFilterService;
        this.deletedObjectService = deletedObjectService;
        this.contextService = contextService;
    }

    @GetMapping
    @PreAuthorize( "hasRole('ALL')" )
    public RootNode getDeletedObjects( DeletedObjectQuery query )
    {
        List<String> fields = Lists.newArrayList( contextService.getParameterValues( "fields" ) );
        int totalDeletedObjects = deletedObjectService.countDeletedObjects( query );
        query.setTotal( totalDeletedObjects );

        if ( fields.isEmpty() )
        {
            fields.addAll( Preset.ALL.getFields() );
        }

        List<DeletedObject> deletedObjects = deletedObjectService.getDeletedObjects( query );

        RootNode rootNode = NodeUtils.createMetadata();

        if ( !query.isSkipPaging() )
        {
            query.setTotal( totalDeletedObjects );
            rootNode.addChild( NodeUtils.createPager( query.getPager() ) );
        }

        rootNode.addChild( fieldFilterService.toCollectionNode( DeletedObject.class, new FieldFilterParams( deletedObjects, fields ) ) );

        return rootNode;
    }
}
