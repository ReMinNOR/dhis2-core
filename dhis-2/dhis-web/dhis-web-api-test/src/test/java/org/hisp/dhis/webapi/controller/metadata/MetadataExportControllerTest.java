package org.hisp.dhis.webapi.controller.metadata;



import org.hisp.dhis.dxf2.metadata.MetadataExportService;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.UserSettingService;
import org.hisp.dhis.webapi.service.ContextService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

/**
 * Unit tests for {@link MetadataExportControllerTest}.
 *
 * @author Volker Schmidt
 */
public class MetadataExportControllerTest
{
    @Mock
    private MetadataExportService metadataExportService;

    @Mock
    private ContextService contextService;

    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private UserSettingService userSettingService;

    @InjectMocks
    private MetadataImportExportController controller;

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void withoutDownload()
    {
        ResponseEntity<RootNode> responseEntity = controller.getMetadata( false, null, false );
        Assert.assertNull( responseEntity.getHeaders().get( HttpHeaders.CONTENT_DISPOSITION ) );
    }

    @Test
    public void withDownload()
    {
        ResponseEntity<RootNode> responseEntity = controller.getMetadata( false, null, true );
        Assert.assertNotNull( responseEntity.getHeaders().get( HttpHeaders.CONTENT_DISPOSITION ) );
        Assert.assertEquals( "attachment; filename=metadata", responseEntity.getHeaders().get( HttpHeaders.CONTENT_DISPOSITION ).get( 0 ) );
    }
}
