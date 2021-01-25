package org.hisp.dhis.webapi.controller.event;



import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hisp.dhis.dxf2.events.relationship.RelationshipService;
import org.hisp.dhis.dxf2.events.trackedentity.Relationship;
import org.hisp.dhis.program.ProgramInstance;
import org.hisp.dhis.program.ProgramInstanceService;
import org.hisp.dhis.program.ProgramStageInstance;
import org.hisp.dhis.program.ProgramStageInstanceService;
import org.hisp.dhis.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.trackedentity.TrackedEntityInstanceService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

/**
 * @author Enrico Colasante
 */
public class RelationshipControllerTest
{
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    private MockMvc mockMvc;

    private static final String TEI_ID = "TEI_ID";
    private static final String EVENT_ID = "EVENT_ID";
    private static final String ENROLLMENT_ID = "ENROLLMENT_ID";
    private static final String REL_ID = "REL_ID";

    private TrackedEntityInstance tei = new TrackedEntityInstance();

    private ProgramInstance enrollment = new ProgramInstance();

    private ProgramStageInstance event = new ProgramStageInstance();

    private Relationship relationship = new Relationship();

    @Mock
    private RelationshipService relationshipService;

    @Mock
    private TrackedEntityInstanceService trackedEntityInstanceService;

    @Mock
    private ProgramInstanceService programInstanceService;

    @Mock
    private ProgramStageInstanceService programStageInstanceService;

    @InjectMocks
    private RelationshipController relationshipController;

    private final static String ENDPOINT = "/relationships";

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup( relationshipController ).build();
    }

    @Test(expected = NestedServletException.class )
    public void verifyEndpointWithNoArgs()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ) );
    }

    @Test(expected = NestedServletException.class )
    public void verifyEndpointWithNotFoundTei()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ).param( "tei", TEI_ID ) );
    }

    @Test
    public void verifyEndpointWithTei()
        throws Exception
    {
        when( trackedEntityInstanceService.getTrackedEntityInstance( TEI_ID )).thenReturn( tei );
        mockMvc.perform( get( ENDPOINT ).param( "tei", TEI_ID ) ).andExpect( status().isOk() );

        verify( trackedEntityInstanceService ).getTrackedEntityInstance( TEI_ID );
        verify( relationshipService ).getRelationshipsByTrackedEntityInstance(tei, false);
    }

    @Test(expected = NestedServletException.class )
    public void verifyEndpointWithNotFoundEvent()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ).param( "event", EVENT_ID ) );
    }

    @Test
    public void verifyEndpointWithEvent()
        throws Exception
    {
        when( programStageInstanceService.getProgramStageInstance( EVENT_ID )).thenReturn( event );
        mockMvc.perform( get( ENDPOINT ).param( "event", EVENT_ID ) ).andExpect( status().isOk() );

        verify( programStageInstanceService ).getProgramStageInstance( EVENT_ID );
        verify( relationshipService ).getRelationshipsByProgramStageInstance(event, false);
    }

    @Test(expected = NestedServletException.class )
    public void verifyEndpointWithNotFoundEnrollment()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ).param( "enrollment", ENROLLMENT_ID ) ).andExpect( status().isBadRequest() );
    }

    @Test
    public void verifyEndpointWithEnrollment()
        throws Exception
    {
        when( programInstanceService.getProgramInstance( ENROLLMENT_ID )).thenReturn( enrollment );
        mockMvc.perform( get( ENDPOINT ).param( "enrollment", ENROLLMENT_ID ) ).andExpect( status().isOk() );

        verify( programInstanceService ).getProgramInstance( ENROLLMENT_ID );
        verify( relationshipService ).getRelationshipsByProgramInstance(enrollment, false);
    }

    @Test(expected = NestedServletException.class )
    public void testGetRelationshipNotPresent()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT + "/" + REL_ID ));
    }

    @Test
    public void testGetRelationship()
        throws Exception
    {
        when( relationshipService.getRelationshipByUid( REL_ID ) ).thenReturn( relationship );
        mockMvc.perform( get( ENDPOINT + "/" + REL_ID )).andExpect( status().isOk() );
    }

    @Test( expected = NestedServletException.class )
    public void testDeleteRelationshipNotPresent()
        throws Exception
    {
        mockMvc.perform( delete( ENDPOINT + "/" + REL_ID )).andExpect( status().isConflict() );
    }

    @Test
    public void testDeleteRelationship()
        throws Exception
    {
        when( relationshipService.getRelationshipByUid( REL_ID ) ).thenReturn( relationship );
        mockMvc.perform( get( ENDPOINT + "/" + REL_ID )).andExpect( status().isOk() );
    }
}
