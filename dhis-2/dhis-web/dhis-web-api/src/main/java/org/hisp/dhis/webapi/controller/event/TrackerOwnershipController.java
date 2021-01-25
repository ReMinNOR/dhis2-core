package org.hisp.dhis.webapi.controller.event;



import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.program.ProgramService;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.trackedentity.TrackerOwnershipManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.hisp.dhis.webapi.service.ContextService;
import org.hisp.dhis.webapi.service.WebMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Ameen Mohamed <ameen@dhis2.org>
 */
@Controller
@RequestMapping( value = TrackerOwnershipController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class TrackerOwnershipController
{
    public static final String RESOURCE_PATH = "/tracker/ownership";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private TrackerOwnershipManager trackerOwnershipAccessManager;

    @Autowired
    protected FieldFilterService fieldFilterService;

    @Autowired
    protected ContextService contextService;

    @Autowired
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Autowired
    private ProgramService programService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private WebMessageService webMessageService;

    // -------------------------------------------------------------------------
    // 1. Transfer ownership if the logged in user is part of the owner ou.
    // 2. Break the glass and override ownership.
    // -------------------------------------------------------------------------

    @RequestMapping( value = "/transfer", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE )
    public void updateTrackerProgramOwner( @RequestParam String trackedEntityInstance, @RequestParam String program,
                                           @RequestParam String ou, HttpServletRequest request, HttpServletResponse response ) {

        trackerOwnershipAccessManager.transferOwnership(
                trackedEntityInstanceService.getTrackedEntityInstance( trackedEntityInstance ),
                programService.getProgram( program ), organisationUnitService.getOrganisationUnit( ou ), false, false );
        webMessageService.send( WebMessageUtils.ok( "Ownership transferred" ), response, request );
    }

    @RequestMapping( value = "/override", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE )
    public void overrideOwnershipAccess( @RequestParam String trackedEntityInstance, @RequestParam String reason,
                                         @RequestParam String program, HttpServletRequest request, HttpServletResponse response ) {

        trackerOwnershipAccessManager.grantTemporaryOwnership(
                trackedEntityInstanceService.getTrackedEntityInstance( trackedEntityInstance ),
                programService.getProgram( program ), currentUserService.getCurrentUser(), reason );

        webMessageService.send( WebMessageUtils.ok( "Temporary Ownership granted" ), response, request );
    }
}
