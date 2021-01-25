package org.hisp.dhis.webapi.service;



import java.util.List;
import java.util.stream.Collectors;

import org.hisp.dhis.common.AccessLevel;
import org.hisp.dhis.dxf2.events.TrackedEntityInstanceParams;
import org.hisp.dhis.dxf2.events.trackedentity.ProgramOwner;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.dxf2.events.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.program.Program;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.trackedentity.TrackedEntityType;
import org.hisp.dhis.trackedentity.TrackedEntityTypeService;
import org.hisp.dhis.trackedentity.TrackerAccessManager;
import org.hisp.dhis.trackedentity.TrackerOwnershipManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.controller.exception.NotFoundException;
import org.springframework.stereotype.Service;

import com.google.common.base.Joiner;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@Service
@RequiredArgsConstructor
public class TrackedEntityInstanceSupportService
{

    private final TrackedEntityInstanceService trackedEntityInstanceService;

    private final CurrentUserService currentUserService;

    private final ProgramService programService;

    private final TrackerAccessManager trackerAccessManager;

    private final org.hisp.dhis.trackedentity.TrackedEntityInstanceService instanceService;

    private final TrackedEntityTypeService trackedEntityTypeService;

    @SneakyThrows
    public TrackedEntityInstance getTrackedEntityInstance( String id, String pr, List<String> fields )
    {
        TrackedEntityInstanceParams trackedEntityInstanceParams = getTrackedEntityInstanceParams( fields );

        TrackedEntityInstance trackedEntityInstance = trackedEntityInstanceService.getTrackedEntityInstance( id,
            trackedEntityInstanceParams );

        if ( trackedEntityInstance == null )
        {
            throw new NotFoundException( "TrackedEntityInstance", id );
        }

        User user = currentUserService.getCurrentUser();

        if ( pr != null )
        {
            Program program = programService.getProgram( pr );

            if ( program == null )
            {
                throw new NotFoundException( "Program", pr );
            }

            List<String> errors = trackerAccessManager.canRead( user,
                instanceService.getTrackedEntityInstance( trackedEntityInstance.getTrackedEntityInstance() ), program,
                false );

            if ( !errors.isEmpty() )
            {
                if ( program.getAccessLevel() == AccessLevel.CLOSED )
                {
                    throw new WebMessageException(
                        WebMessageUtils.unathorized( TrackerOwnershipManager.PROGRAM_ACCESS_CLOSED ) );
                }
                throw new WebMessageException(
                    WebMessageUtils.unathorized( TrackerOwnershipManager.OWNERSHIP_ACCESS_DENIED ) );
            }

            if ( trackedEntityInstanceParams.isIncludeProgramOwners() )
            {
                List<ProgramOwner> filteredProgramOwners = trackedEntityInstance.getProgramOwners().stream()
                    .filter( tei -> tei.getProgram().equals( pr ) ).collect( Collectors.toList() );
                trackedEntityInstance.setProgramOwners( filteredProgramOwners );
            }
        }
        else
        {
            // return only tracked entity type attributes

            TrackedEntityType trackedEntityType = trackedEntityTypeService
                .getTrackedEntityType( trackedEntityInstance.getTrackedEntityType() );

            if ( trackedEntityType != null )
            {
                List<String> tetAttributes = trackedEntityType.getTrackedEntityAttributes().stream()
                    .map( TrackedEntityAttribute::getUid ).collect( Collectors.toList() );

                trackedEntityInstance.setAttributes( trackedEntityInstance.getAttributes().stream()
                    .filter( att -> tetAttributes.contains( att.getAttribute() ) ).collect( Collectors.toList() ) );
            }
        }

        return trackedEntityInstance;
    }

    public TrackedEntityInstanceParams getTrackedEntityInstanceParams( List<String> fields )
    {
        String joined = Joiner.on( "" ).join( fields );

        if ( joined.contains( "*" ) )
        {
            return TrackedEntityInstanceParams.TRUE;
        }

        TrackedEntityInstanceParams params = new TrackedEntityInstanceParams();

        if ( joined.contains( "relationships" ) )
        {
            params.setIncludeRelationships( true );
        }

        if ( joined.contains( "enrollments" ) )
        {
            params.setIncludeEnrollments( true );
        }

        if ( joined.contains( "events" ) )
        {
            params.setIncludeEvents( true );
        }

        if ( joined.contains( "programOwners" ) )
        {
            params.setIncludeProgramOwners( true );
        }

        return params;
    }

}
