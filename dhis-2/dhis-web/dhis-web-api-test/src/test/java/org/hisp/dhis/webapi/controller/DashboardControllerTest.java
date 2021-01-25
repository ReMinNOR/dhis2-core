package org.hisp.dhis.webapi.controller;



import com.google.common.net.HttpHeaders;
import org.hisp.dhis.dashboard.Dashboard;
import org.hisp.dhis.dashboard.DashboardService;
import org.hisp.dhis.dxf2.metadata.MetadataExportParams;
import org.hisp.dhis.dxf2.metadata.MetadataExportService;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.ContextService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link DashboardController}.
 *
 * @author Volker Schmidt
 */
public class DashboardControllerTest
{
    @Mock
    private ContextService contextService;

    @Mock
    private MetadataExportService exportService;

    @Mock
    private DashboardService service;

    @Mock
    private Dashboard dashboard;

    @InjectMocks
    private DashboardController controller;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getWithDependencies() throws Exception
    {
        getWithDependencies( false );
    }

    @Test
    public void getWithDependenciesAsDownload() throws Exception
    {
        getWithDependencies( true );
    }

    private void getWithDependencies( boolean download ) throws Exception
    {
        final Map<String, List<String>> parameterValuesMap = new HashMap<>();
        final MetadataExportParams exportParams = new MetadataExportParams();
        final RootNode rootNode = new RootNode( "test" );

        Mockito.when( service.getDashboard( Mockito.eq( "88dshgdga" ) ) ).thenReturn( dashboard );
        Mockito.when( contextService.getParameterValuesMap() ).thenReturn( parameterValuesMap );
        Mockito.when( exportService.getParamsFromMap( Mockito.same( parameterValuesMap ) ) ).thenReturn( exportParams );
        Mockito.when( exportService.getMetadataWithDependenciesAsNode( Mockito.same( dashboard ), Mockito.same( exportParams ) ) )
            .thenReturn( rootNode );

        final ResponseEntity<RootNode> responseEntity = controller.getDataSetWithDependencies( "88dshgdga", download );
        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertSame( rootNode, responseEntity.getBody() );
        if ( download )
        {
            Assert.assertEquals( "attachment; filename=metadata",
                responseEntity.getHeaders().getFirst( HttpHeaders.CONTENT_DISPOSITION ) );
        }
        else
        {
            Assert.assertFalse( responseEntity.getHeaders().containsKey( HttpHeaders.CONTENT_DISPOSITION ) );
        }
    }
}
