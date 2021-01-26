package org.hisp.dhis.webapi.controller.dataitem;

import static com.google.common.collect.Sets.newHashSet;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hisp.dhis.common.DimensionItemType.INDICATOR;
import static org.hisp.dhis.webapi.controller.dataitem.DataItemServiceFacade.DATA_TYPE_ENTITY_MAP;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE_SIZE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.junit.MockitoJUnit.rule;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.DimensionItemType;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dataitem.query.QueryExecutor;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoRule;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

public class DataItemServiceFacadeTest
{
    @Mock
    private CurrentUserService currentUserService;

    @Mock
    private AclService aclService;

    @Mock
    private QueryExecutor queryExecutor;

    @Rule
    public MockitoRule mockitoRule = rule();

    private DataItemServiceFacade dataItemServiceFacade;

    @Before
    public void setUp()
    {
        dataItemServiceFacade = new DataItemServiceFacade( currentUserService, aclService, queryExecutor );
    }

    @Test
    public void testRetrieveDataItemEntities()
    {
        // Given
        final Class<? extends BaseDimensionalItemObject> targetEntity = Indicator.class;
        final Set<Class<? extends BaseDimensionalItemObject>> anyTargetEntities = new HashSet<>(
            asList( targetEntity ) );
        final List<DataItem> expectedItemsFound = asList( mockDataItem( INDICATOR ), mockDataItem( INDICATOR ) );
        final Set<String> anyFilters = newHashSet( "anyFilter" );
        final WebOptions anyWebOptions = mockWebOptions( 10, 1 );
        final Set<String> anyOrdering = new HashSet<>( asList( "name:desc" ) );
        final OrderParams anyOrderParams = new OrderParams( anyOrdering );
        final User currentUser = new User();

        // When
        when( currentUserService.getCurrentUser() ).thenReturn( currentUser );
        when( aclService.canRead( currentUser, targetEntity ) ).thenReturn( true );
        when( queryExecutor.find( any( Class.class ), any( MapSqlParameterSource.class ) ) )
            .thenReturn( expectedItemsFound );
        final List<DataItem> actualDimensionalItems = dataItemServiceFacade
            .retrieveDataItemEntities( anyTargetEntities, anyFilters, anyWebOptions, anyOrderParams );

        // Then
        assertThat( actualDimensionalItems, hasSize( 2 ) );
        assertThat( actualDimensionalItems.get( 0 ).getDimensionItemType(), is( INDICATOR.name() ) );
        assertThat( actualDimensionalItems.get( 1 ).getDimensionItemType(), is( INDICATOR.name() ) );
    }

    @Test
    public void testRetrieveDataItemEntitiesWhenTargetEntitiesIsEmpty()
    {
        // Given
        final Set<Class<? extends BaseDimensionalItemObject>> anyTargetEntities = emptySet();
        final Set<String> anyFilters = newHashSet( "anyFilter" );
        final WebOptions anyWebOptions = mockWebOptions( 10, 1 );
        final Set<String> anyOrdering = new HashSet<>( asList( "name:desc" ) );
        final OrderParams anyOrderParams = new OrderParams( anyOrdering );

        // When
        final List<DataItem> actualDimensionalItems = dataItemServiceFacade
            .retrieveDataItemEntities( anyTargetEntities, anyFilters, anyWebOptions, anyOrderParams );

        // Then
        assertThat( actualDimensionalItems, is( empty() ) );
    }

    @Test
    public void testExtractTargetEntitiesUsingEqualsFilter()
    {
        // Given
        final Set<Class<? extends BaseDimensionalItemObject>> expectedTargetEntities = new HashSet<>(
            asList( Indicator.class ) );
        final Set<String> theFilters = newHashSet( "dimensionItemType:eq:INDICATOR" );

        // When
        final Set<Class<? extends BaseDimensionalItemObject>> actualTargetEntities = dataItemServiceFacade
            .extractTargetEntities( theFilters );

        // Then
        assertThat( actualTargetEntities, containsInAnyOrder( expectedTargetEntities.toArray() ) );
    }

    @Test
    public void testExtractTargetEntitiesUsingInFilter()
    {
        // Given
        final Set<Class<? extends BaseDimensionalItemObject>> expectedTargetEntities = new HashSet<>(
            asList( Indicator.class, DataSet.class ) );
        final Set<String> theFilters = newHashSet( "dimensionItemType:in:[INDICATOR, DATA_SET]" );

        // When
        final Set<Class<? extends BaseDimensionalItemObject>> actualTargetEntities = dataItemServiceFacade
            .extractTargetEntities( theFilters );

        // Then
        assertThat( actualTargetEntities, containsInAnyOrder( expectedTargetEntities.toArray() ) );
    }

    @Test
    public void testExtractTargetEntitiesWhenThereIsNoExplicitTargetSet()
    {
        // Given
        final Set<String> noTargetEntitiesFilters = emptySet();

        // When
        final Set<Class<? extends BaseDimensionalItemObject>> actualTargetEntities = dataItemServiceFacade
            .extractTargetEntities( noTargetEntitiesFilters );

        // Then
        assertThat( actualTargetEntities, containsInAnyOrder( DATA_TYPE_ENTITY_MAP.values().toArray() ) );
    }

    private WebOptions mockWebOptions( final int pageSize, final int pageNumber )
    {
        final Map<String, String> options = new HashMap<>( 0 );
        options.put( PAGE_SIZE, valueOf( pageSize ) );
        options.put( PAGE, valueOf( pageNumber ) );
        options.put( PAGING, "true" );

        return new WebOptions( options );
    }

    private DataItem mockDataItem( final DimensionItemType dimensionItemType )
    {
        final DataItem dataItem = new DataItem();
        dataItem.setDimensionItemType( dimensionItemType.name() );

        return dataItem;
    }
}
