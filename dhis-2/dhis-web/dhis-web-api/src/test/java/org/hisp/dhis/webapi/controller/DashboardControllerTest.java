package org.hisp.dhis.webapi.controller;



import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.dashboard.DashboardItemType;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.metadata.MetadataExportService;
import org.hisp.dhis.webapi.service.ContextService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.google.common.collect.Sets;

/**
 * @author Luciano Fiandesio
 */
public class DashboardControllerTest
{
    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @Mock
    protected IdentifiableObjectManager manager;

    @Mock
    protected MetadataExportService exportService;

    @Mock
    protected ContextService contextService;

    @InjectMocks
    private DashboardController dashboardController;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    private final static String ENDPOINT = "/dashboards/q";

    @Before
    public void setUp()
    {
        mockMvc = MockMvcBuilders.standaloneSetup( dashboardController ).build();
    }

    @Test
    public void verifyEndpointWithNoArgs()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ) ).andExpect( status().isOk() );

        verify( dashboardService ).search( null, null, null );
    }

    @Test
    public void verifyEndpointWithMaxArg()
        throws Exception
    {
        mockMvc.perform( get( ENDPOINT ).param( "max", "CHART" ) ).andExpect( status().isOk() );

        verify( dashboardService ).search( Sets.newHashSet( DashboardItemType.CHART ), null, null );
    }

    @Test
    public void verifyEndpointWithAllArg()
        throws Exception
    {
        mockMvc.perform(
            get( ENDPOINT )
                .param( "max", "CHART" )
                .param( "count", "10" )
                .param( "maxCount", "20" ) )
            .andExpect( status().isOk() );

        verify( dashboardService ).search( Sets.newHashSet( DashboardItemType.CHART ), 10, 20 );
    }

    @Test
    public void verifyEndpointWithSearchQueryWithNoArgs()
            throws Exception
    {
        mockMvc.perform( get( ENDPOINT + "/alfa" ) ).andExpect( status().isOk() );

        verify( dashboardService ).search( "alfa" , null, null, null );
    }

    @Test
    public void verifyEndpointWithSearchQueryWithMaxArg()
            throws Exception
    {
        mockMvc.perform(
            get( ENDPOINT + "/alfa" )
                .param( "max", "CHART" ) )
            .andExpect( status().isOk() );

        verify( dashboardService ).search( "alfa" , Sets.newHashSet( DashboardItemType.CHART ), null, null );
    }

    @Test
    public void verifyEndpointWithSearchQueryWithAllArg()
            throws Exception
    {
        mockMvc.perform(
            get( ENDPOINT + "/alfa"  )
                .param( "max", "CHART" )
                .param( "count", "10" )
                .param( "maxCount", "20" ) )
            .andExpect( status().isOk() );

        verify( dashboardService ).search( "alfa" , Sets.newHashSet( DashboardItemType.CHART ), 10, 20 );
    }


}
