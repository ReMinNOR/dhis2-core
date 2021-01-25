package org.hisp.dhis.webapi.controller.metadata;



import com.google.common.net.HttpHeaders;
import org.hisp.dhis.attribute.Attribute;
import org.hisp.dhis.dxf2.metadata.MetadataExportParams;
import org.hisp.dhis.dxf2.metadata.MetadataExportService;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.ContextService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
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
 * Unit tests for {@link MetadataExportControllerUtils}.
 *
 * @author Volker Schmidt
 */
public class MetadataExportControllerUtilsTest
{
    @Mock
    private ContextService contextService;

    @Mock
    private MetadataExportService exportService;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test
    public void getWithDependencies()
    {
        final Map<String, List<String>> parameterValuesMap = new HashMap<>();
        final MetadataExportParams exportParams = new MetadataExportParams();
        final Attribute attribute = new Attribute();
        final RootNode rootNode = new RootNode( "test" );

        Mockito.when( contextService.getParameterValuesMap() ).thenReturn( parameterValuesMap );
        Mockito.when( exportService.getParamsFromMap( Mockito.same( parameterValuesMap ) ) ).thenReturn( exportParams );
        Mockito.when( exportService.getMetadataWithDependenciesAsNode( Mockito.same( attribute ), Mockito.same( exportParams ) ) )
            .thenReturn( rootNode );

        final ResponseEntity<RootNode> responseEntity =
            MetadataExportControllerUtils.getWithDependencies( contextService, exportService, attribute, false );
        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertSame( rootNode, responseEntity.getBody() );
        Assert.assertFalse( responseEntity.getHeaders().containsKey( HttpHeaders.CONTENT_DISPOSITION ) );
    }

    @Test
    public void getWithDependenciesAsDownload()
    {
        final Map<String, List<String>> parameterValuesMap = new HashMap<>();
        final MetadataExportParams exportParams = new MetadataExportParams();
        final Attribute attribute = new Attribute();
        final RootNode rootNode = new RootNode( "test" );

        Mockito.when( contextService.getParameterValuesMap() ).thenReturn( parameterValuesMap );
        Mockito.when( exportService.getParamsFromMap( Mockito.same( parameterValuesMap ) ) ).thenReturn( exportParams );
        Mockito.when( exportService.getMetadataWithDependenciesAsNode( Mockito.same( attribute ), Mockito.same( exportParams ) ) )
            .thenReturn( rootNode );

        final ResponseEntity<RootNode> responseEntity =
            MetadataExportControllerUtils.getWithDependencies( contextService, exportService, attribute, true );
        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertSame( rootNode, responseEntity.getBody() );
        Assert.assertEquals( "attachment; filename=metadata",
            responseEntity.getHeaders().getFirst( HttpHeaders.CONTENT_DISPOSITION ) );
    }

    @Test
    public void createResponseEntity()
    {
        final RootNode rootNode = new RootNode( "test" );
        final ResponseEntity<RootNode> responseEntity =
            MetadataExportControllerUtils.createResponseEntity( rootNode, false );
        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertSame( rootNode, responseEntity.getBody() );
        Assert.assertFalse( responseEntity.getHeaders().containsKey( HttpHeaders.CONTENT_DISPOSITION ) );
    }

    @Test
    public void createResponseEntityAsDownload()
    {
        final RootNode rootNode = new RootNode( "test" );
        final ResponseEntity<RootNode> responseEntity =
            MetadataExportControllerUtils.createResponseEntity( rootNode, true );
        Assert.assertEquals( HttpStatus.OK, responseEntity.getStatusCode() );
        Assert.assertSame( rootNode, responseEntity.getBody() );
        Assert.assertEquals( "attachment; filename=metadata",
            responseEntity.getHeaders().getFirst( HttpHeaders.CONTENT_DISPOSITION ) );
    }
}
