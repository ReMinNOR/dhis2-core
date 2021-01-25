package org.hisp.dhis.webapi.controller.tracker;



import static org.hisp.dhis.webapi.controller.tracker.TrackerImportController.TRACKER_JOB_ADDED;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.UUID;

import org.hisp.dhis.commons.config.JacksonObjectMapperConfig;
import org.hisp.dhis.render.DefaultRenderService;
import org.hisp.dhis.render.RenderService;
import org.hisp.dhis.schema.SchemaService;
import org.hisp.dhis.system.notification.Notifier;
import org.hisp.dhis.tracker.DefaultTrackerImportService;
import org.hisp.dhis.tracker.job.TrackerMessageManager;
import org.hisp.dhis.tracker.report.TrackerBundleReport;
import org.hisp.dhis.tracker.report.TrackerImportReport;
import org.hisp.dhis.tracker.report.TrackerStatus;
import org.hisp.dhis.tracker.report.TrackerTimingsStats;
import org.hisp.dhis.tracker.report.TrackerValidationReport;
import org.hisp.dhis.webapi.service.DefaultContextService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * @author Giuseppe Nespolino <g.nespolino@gmail.com>
 */
public class TrackerImportControllerTest
{
    private final static String ENDPOINT = "/" + TrackerControllerSupport.RESOURCE_PATH;

    private MockMvc mockMvc;

    @Mock
    private DefaultTrackerImportService trackerImportService;

    @Mock
    private TrackerMessageManager trackerMessageManager;

    @Mock
    private Notifier notifier;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private RenderService renderService;

    private final static TrackerImportReport TRACKER_IMPORT_REPORT = TrackerImportReport.withImportCompleted(
        TrackerStatus.OK,
        TrackerBundleReport.builder()
            .status( TrackerStatus.OK )
            .build(),
        TrackerValidationReport.builder()
            .build(),
        new TrackerTimingsStats(),
        new HashMap<>() );

    @Before
    public void setUp()
    {
        renderService = new DefaultRenderService( JacksonObjectMapperConfig.jsonMapper,
            JacksonObjectMapperConfig.xmlMapper,
            mock( SchemaService.class ) );

        // Controller under test
        final TrackerImportController controller = new TrackerImportController( trackerImportService, renderService,
            new DefaultContextService(), trackerMessageManager, notifier );

        mockMvc = MockMvcBuilders.standaloneSetup( controller ).build();

    }

    @Test
    public void verifyAsync()
        throws Exception
    {
        // When
        when( trackerMessageManager.addJob( any() ) ).thenReturn( UUID.randomUUID().toString() );

        // Then
        mockMvc.perform( post( ENDPOINT )
            .content( "{}" )
            .contentType( MediaType.APPLICATION_JSON )
            .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.message" ).value( TRACKER_JOB_ADDED ) )
            .andExpect( content().contentType( "application/json" ) );
    }

    @Test
    public void verifySync()
        throws Exception
    {
        // When
        when( trackerImportService.importTracker( any() ) ).thenReturn( TRACKER_IMPORT_REPORT );
        when( trackerImportService.buildImportReport( any(), any() ) ).thenCallRealMethod();

        // Then
        String contentAsString = mockMvc.perform( post( ENDPOINT + "?async=false" )
            .content( "{}" )
            .contentType( MediaType.APPLICATION_JSON )
            .accept( MediaType.APPLICATION_JSON ) )
            .andExpect( status().isOk() )
            .andExpect( jsonPath( "$.message" ).doesNotExist() )
            .andExpect( content().contentType( "application/json" ) )
            .andReturn()
            .getResponse()
            .getContentAsString();

        try  {
            renderService.fromJson( contentAsString, TrackerImportReport.class );
        } catch (Exception e) {
            fail( "response content : " + contentAsString + "\n" + " is not of TrackerImportReport type" );
        }
    }

}
