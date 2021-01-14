package org.hisp.dhis.webapi.controller.tracker.export;

/*
 *  Copyright (c) 2004-2020, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.badRequest;
import static org.hisp.dhis.dxf2.webmessage.WebMessageUtils.notFound;
import static org.hisp.dhis.webapi.controller.tracker.TrackerControllerSupport.RESOURCE_PATH;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;

import org.hisp.dhis.dxf2.events.relationship.RelationshipService;
import org.hisp.dhis.dxf2.events.trackedentity.Relationship;
import org.hisp.dhis.dxf2.webmessage.WebMessage;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStage;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.tracker.domain.mapper.RelationshipMapper;
import org.mapstruct.factory.Mappers;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RestController
@RequestMapping( value = RESOURCE_PATH + "/" + TrackerRelationshipsExportController.RELATIONSHIPS )
@RequiredArgsConstructor
public class TrackerRelationshipsExportController
{
    protected final static String RELATIONSHIPS = "relationships";

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    private final ProgramInstanceService programInstanceService;

    private final ProgramStageInstanceService programStageInstanceService;

    private final RelationshipService relationshipService;

    private static final RelationshipMapper RELATIONSHIP_MAPPER = Mappers.getMapper( RelationshipMapper.class );

    private Map<Class<?>, Function<String, ?>> objectRetrievers;

    private Map<Class<?>, Function<Object, List<Relationship>>> relationshipRetrievers;

    @PostConstruct
    void setupMaps()
    {
        objectRetrievers = ImmutableMap.<Class<?>, Function<String, ?>> builder()
            .put( TrackedEntityInstance.class, trackedEntityInstanceService::getTrackedEntityInstance )
            .put( ProgramInstance.class, programInstanceService::getProgramInstance )
            .put( ProgramStageInstanceService.class, programStageInstanceService::getProgramStageInstance )
            .build();

        relationshipRetrievers = ImmutableMap.<Class<?>, Function<Object, List<Relationship>>> builder()
            .put( TrackedEntityInstance.class,
                o -> relationshipService.getRelationshipsByTrackedEntityInstance( (TrackedEntityInstance) o, false ) )
            .put( ProgramStage.class,
                o -> relationshipService.getRelationshipsByProgramInstance( (ProgramInstance) o, false ) )
            .put( ProgramStageInstance.class,
                o -> relationshipService.getRelationshipsByProgramStageInstance( (ProgramStageInstance) o, false ) )
            .build();
    }

    @GetMapping( produces = APPLICATION_JSON_VALUE )
    List<org.hisp.dhis.tracker.domain.Relationship> getInstances(
        @RequestParam( required = false ) String tei,
        @RequestParam( required = false ) String enrollment,
        @RequestParam( required = false ) String event )
        throws WebMessageException
    {

        List<org.hisp.dhis.tracker.domain.Relationship> relationships = tryGetRelationshipFrom(
            tei,
            TrackedEntityInstance.class,
            () -> notFound( "No trackedEntityInstance '" + tei + "' found." ) );

        if ( Objects.isNull( relationships ) )
        {
            relationships = tryGetRelationshipFrom(
                enrollment,
                ProgramInstance.class,
                () -> notFound( "No enrollment '" + enrollment + "' found." ) );
        }

        if ( Objects.isNull( relationships ) )
        {
            relationships = tryGetRelationshipFrom(
                event,
                ProgramStageInstance.class,
                () -> notFound( "No event '" + event + "' found." ) );
        }

        if ( Objects.isNull( relationships ) )
        {
            throw new WebMessageException( badRequest( "Missing required parameter 'tei', 'enrollment' or 'event'." ) );
        }

        return relationships;
    }

    @SneakyThrows
    private List<org.hisp.dhis.tracker.domain.Relationship> tryGetRelationshipFrom(
        String identifier,
        Class<?> type,
        Supplier<WebMessage> notFoundMessageSupplier )
    {
        if ( identifier != null )
        {
            Object object = getObjectRetriever( type ).apply( identifier );
            if ( object != null )
            {
                return RELATIONSHIP_MAPPER.fromCollection( getRelationshipRetriever( type ).apply( object ) );
            }
            else
            {
                throw new WebMessageException( notFoundMessageSupplier.get() );
            }
        }
        return null;
    }

    private Function<Object, List<Relationship>> getRelationshipRetriever( Class<?> type )
    {
        return Optional.ofNullable( type )
            .map( relationshipRetrievers::get )
            .orElseThrow(
                () -> new IllegalArgumentException( "Unable to detect relationship retriever from " + type ) );
    }

    private Function<String, ?> getObjectRetriever( Class<?> type )
    {
        return Optional.ofNullable( type )
            .map( objectRetrievers::get )
            .orElseThrow( () -> new IllegalArgumentException( "Unable to detect object retriever from " + type ) );
    }
}
