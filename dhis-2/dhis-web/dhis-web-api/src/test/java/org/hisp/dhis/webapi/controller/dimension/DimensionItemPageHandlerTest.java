package org.hisp.dhis.webapi.controller.dimension;



import static java.lang.String.valueOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE_SIZE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.junit.MockitoJUnit.rule;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.common.Pager;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.LinkService;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;

/**
 * @author maikel arabori
 */
public class DimensionItemPageHandlerTest
{

    @Mock
    private LinkService linkService;

    @Rule
    public MockitoRule mockitoRule = rule();

    private DimensionItemPageHandler dimensionItemPageHandler;

    @Before
    public void setUp()
    {
        dimensionItemPageHandler = new DimensionItemPageHandler( linkService );
    }

    @Test
    public void testAddPaginationToNodeWithSuccess()
    {
        // Given
        final RootNode anyRootNode = new RootNode( "any" );
        final WebOptions anyWebOptions = mockWebOptions( 10, 1 );
        final String anyUid = "LFsZ8v5v7rq";
        final int anyTotals = 12;

        // When
        dimensionItemPageHandler.addPaginationToNodeIfEnabled( anyRootNode, anyWebOptions,
            anyUid, anyTotals );

        // Then
        assertThat( anyRootNode, is( notNullValue() ) );
        assertThat( anyRootNode.getName(), is( equalTo( "any" ) ) );
        assertThat( anyRootNode.getChildren(), hasSize( 1 ) );
        assertThat( anyRootNode.getChildren().get( 0 ).isComplex(), is( true ) );
        verify( linkService, times( 1 ) ).generatePagerLinks( any( Pager.class ), anyString() );
    }

    @Test
    public void testAddPaginationToNodeWhenPagingIsFalse()
    {
        // Given
        final RootNode anyRootNode = new RootNode( "any" );
        final WebOptions webOptionsNoPaging = mockWebOptionsWithPagingFlagFalse();
        final String anyUid = "LFsZ8v5v7rq";
        final int anyTotals = 12;

        // When
        dimensionItemPageHandler.addPaginationToNodeIfEnabled( anyRootNode, webOptionsNoPaging,
            anyUid, anyTotals );

        // Then
        assertThat( anyRootNode, is( notNullValue() ) );
        assertThat( anyRootNode.getName(), is( equalTo( "any" ) ) );
        assertThat( anyRootNode.getChildren(), is( empty() ) );
        verify( linkService, never() ).generatePagerLinks( any( Pager.class ), anyString() );
    }

    @Test
    public void testAddPaginationToNodeWhenNoPagingIsSet()
    {
        // Given
        final RootNode anyRootNode = new RootNode( "any" );
        final WebOptions webOptionsNoPaging = mockWebOptionsWithNoPagingFlagSet();
        final String anyUid = "LFsZ8v5v7rq";
        final int anyTotals = 12;

        // When
        dimensionItemPageHandler.addPaginationToNodeIfEnabled( anyRootNode, webOptionsNoPaging,
            anyUid, anyTotals );

        // Then
        assertThat( anyRootNode, is( notNullValue() ) );
        assertThat( anyRootNode.getName(), is( equalTo( "any" ) ) );
        assertThat( anyRootNode.getChildren(), is( empty() ) );
        verify( linkService, never() ).generatePagerLinks( any( Pager.class ), anyString() );
    }

    private WebOptions mockWebOptions( final int pageSize, final int pageNumber )
    {
        final Map<String, String> options = new HashMap<>( 0 );
        options.put( PAGE_SIZE, valueOf( pageSize ) );
        options.put( PAGE, valueOf( pageNumber ) );
        options.put( PAGING, "true" );

        return new WebOptions( options );
    }

    private WebOptions mockWebOptionsWithPagingFlagFalse()
    {
        final Map<String, String> options = new HashMap<>( 0 );
        options.put( PAGING, "false" );

        return new WebOptions( options );
    }

    private WebOptions mockWebOptionsWithNoPagingFlagSet()
    {
        final Map<String, String> options = new HashMap<>( 0 );

        return new WebOptions( options );
    }
}
