package org.hisp.dhis.webapi.controller.tracker.export;



import static org.hisp.dhis.webapi.controller.tracker.TrackerControllerSupport.RESOURCE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.List;

import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceQueryParams;
import org.hisp.dhis.tracker.domain.TrackedEntity;
import org.hisp.dhis.tracker.domain.mapper.TrackedEntityMapper;
import org.hisp.dhis.tracker.domain.web.PagingWrapper;
import org.hisp.dhis.webapi.controller.event.TrackedEntityInstanceCriteria;
import org.hisp.dhis.webapi.controller.event.mapper.TrackedEntityCriteriaMapper;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.service.TrackedEntityInstanceSupportService;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping( value = RESOURCE_PATH + "/" + TrackerTrackedEntitiesExportController.TRACKED_ENTITIES )
@RequiredArgsConstructor
public class TrackerTrackedEntitiesExportController
{
    protected final static String TRACKED_ENTITIES = "trackedEntities";

    private final ContextService contextService;

    private final TrackedEntityCriteriaMapper criteriaMapper;

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    private static final TrackedEntityMapper TRACKED_ENTITY_MAPPER = Mappers.getMapper( TrackedEntityMapper.class );

    private final TrackedEntityInstanceSupportService trackedEntityInstanceSupportService;

    @GetMapping( produces = APPLICATION_JSON_VALUE )
    PagingWrapper<TrackedEntity> getInstances( TrackedEntityInstanceCriteria criteria )
    {
        List<String> fields = contextService.getFieldsFromRequestOrAll();

        TrackedEntityInstanceQueryParams queryParams = criteriaMapper.map( criteria );

        Collection<TrackedEntity> trackedEntityInstances = TRACKED_ENTITY_MAPPER
            .fromCollection( trackedEntityInstanceService.getTrackedEntityInstances( queryParams,
                trackedEntityInstanceSupportService.getTrackedEntityInstanceParams( fields ), false ) );

        int count = trackedEntityInstanceService.getTrackedEntityInstanceCount( queryParams, true, false );

        PagingWrapper<TrackedEntity> trackedEntityInstancePagingWrapper = new PagingWrapper<>();

        if ( queryParams.isPaging() && queryParams.isTotalPages() )
        {
            trackedEntityInstancePagingWrapper = trackedEntityInstancePagingWrapper.withPager(
                new Pager( queryParams.getPageWithDefault(), count, queryParams.getPageSizeWithDefault() ) );
        }

        return trackedEntityInstancePagingWrapper.withInstances( trackedEntityInstances );
    }

    @GetMapping( value = "{id}" )
    public TrackedEntity getTrackedEntityInstanceById( @PathVariable String id,
        @RequestParam( required = false ) String program )
    {
        return TRACKED_ENTITY_MAPPER.from( trackedEntityInstanceSupportService.getTrackedEntityInstance( id, program,
            contextService.getFieldsFromRequestOrAll() ) );
    }
}
