package org.hisp.dhis.webapi.service;



import org.hisp.dhis.common.Pager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.schema.Schema;
import org.hisp.dhis.schema.SchemaService;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link DefaultLinkService}.
 *
 * @author Volker Schmidt
 */
public class DefaultLinkServiceTest
{
    @Mock
    private SchemaService schemaService;

    @Mock
    private ContextService contextService;

    @InjectMocks
    private DefaultLinkService service;

    private MockHttpServletRequest request = new MockHttpServletRequest();

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Test
    public void noLinks()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "/organizationUnits" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        final Pager pager = new Pager();
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertNull( pager.getPrevPage() );
        Assert.assertNull( pager.getNextPage() );
    }

    @Test
    public void nextLinkDefaultParameters()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "/organizationUnits" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            return map;
        } );

        final Pager pager = new Pager( 1, 1000 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertNull( pager.getPrevPage() );
        Assert.assertEquals( "/demo/api/456/organizationUnits?page=2", pager.getNextPage() );
    }

    @Test
    public void nextLinkParameters()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "/organizationUnits.json" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            map.put( "fields", Collections.singletonList( "id,name,value[id,text]" ) );
            map.put( "value[x]", Arrays.asList( "test1", "test2\u00D8" ) );
            return map;
        } );

        final Pager pager = new Pager( 1, 1000 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertNull( pager.getPrevPage() );
        Assert.assertEquals( "/demo/api/456/organizationUnits.json?page=2&fields=id%2Cname%2Cvalue%5Bid%2Ctext%5D&value%5Bx%5D=test1&value%5Bx%5D=test2%C3%98", pager.getNextPage() );
    }

    @Test
    public void prevLinkDefaultParameters()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "/organizationUnits.xml" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            return map;
        } );

        final Pager pager = new Pager( 2, 60 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertEquals( "/demo/api/456/organizationUnits.xml", pager.getPrevPage() );
        Assert.assertNull( pager.getNextPage() );
    }

    @Test
    public void nextLink()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "/organizationUnits.xml.gz" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            return map;
        } );

        final Pager pager = new Pager( 2, 60 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertEquals( "/demo/api/456/organizationUnits.xml.gz", pager.getPrevPage() );
        Assert.assertNull( pager.getNextPage() );
    }

    @Test
    public void nextLinkWithDotsInPath()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        request.setRequestURI( "https://play.dhis2.org/2.30/api/30/organizationUnits.xml.gz" );
        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/2.30/api/30" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            return map;
        } );

        final Pager pager = new Pager( 2, 60 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertEquals( "/2.30/api/30/organizationUnits.xml.gz", pager.getPrevPage() );
        Assert.assertNull( pager.getNextPage() );
    }

    @Test
    public void prevLinkParameters()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            map.put( "fields", Collections.singletonList( "id,name,value[id,text]" ) );
            map.put( "value[x]", Arrays.asList( "test1", "test2\u00D8" ) );
            return map;
        } );

        final Pager pager = new Pager( 3, 110 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertNull( pager.getNextPage() );
        Assert.assertEquals( "/demo/api/456/organizationUnits?page=2&fields=id%2Cname%2Cvalue%5Bid%2Ctext%5D&value%5Bx%5D=test1&value%5Bx%5D=test2%C3%98", pager.getPrevPage() );
    }

    @Test
    public void prevLinkParametersPage1()
    {
        Mockito.when( schemaService.getDynamicSchema( Mockito.eq( OrganisationUnit.class ) ) ).thenAnswer( invocation -> {
            Schema schema = new Schema( OrganisationUnit.class, "organisationUnit", "organisationUnits" );
            schema.setRelativeApiEndpoint( "/organizationUnits" );
            return schema;
        } );

        Mockito.when( contextService.getRequest() ).thenReturn( request );

        Mockito.when( contextService.getApiPath() ).thenReturn( "/demo/api/456" );

        Mockito.when( contextService.getParameterValuesMap() ).thenAnswer( invocation -> {
            final Map<String, List<String>> map = new HashMap<>();
            map.put( "page", Collections.singletonList( "1" ) );
            map.put( "pageSize", Collections.singletonList( "55" ) );
            map.put( "fields", Collections.singletonList( "id,name,value[id,text]" ) );
            map.put( "value[x]", Arrays.asList( "test1", "test2\u00D8" ) );
            return map;
        } );

        final Pager pager = new Pager( 2, 90 );
        service.generatePagerLinks( pager, OrganisationUnit.class );
        Assert.assertNull( pager.getNextPage() );
        Assert.assertEquals( "/demo/api/456/organizationUnits?fields=id%2Cname%2Cvalue%5Bid%2Ctext%5D&value%5Bx%5D=test1&value%5Bx%5D=test2%C3%98", pager.getPrevPage() );
    }
}
