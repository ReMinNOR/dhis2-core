package org.hisp.dhis.webapi.controller.dataitem.helper;



import static java.lang.String.valueOf;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hisp.dhis.webapi.controller.dataitem.helper.PaginationHelper.slice;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGE_SIZE;
import static org.hisp.dhis.webapi.webdomain.WebOptions.PAGING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.junit.Test;

public class PaginationHelperTest
{
    @Test
    public void testSliceWhenFirstPage()
    {
        // Given
        final int pageSize = 5;
        final int firstPage = 1;
        final int totalOfItems = 13;

        final WebOptions theWebOptions = mockWebOptions( pageSize, firstPage );
        final List<DataItem> anyDimensionalItems = mockDimensionalItems( totalOfItems );

        // When
        final List<DataItem> resultingList = slice( theWebOptions,
            anyDimensionalItems );

        // Then
        assertThat( resultingList, hasSize( 5 ) );
    }

    @Test
    public void testSliceWhenIntermediatePage()
    {
        // Given
        final int pageSize = 5;
        final int secondPage = 2;
        final int totalOfItems = 13;

        final WebOptions theWebOptions = mockWebOptions( pageSize, secondPage );
        final List<DataItem> anyDimensionalItems = mockDimensionalItems( totalOfItems );

        // When
        final List<DataItem> resultingList = slice( theWebOptions,
            anyDimensionalItems );

        // Then
        assertThat( resultingList, hasSize( 5 ) );
    }

    @Test
    public void testSliceWhenLastPage()
    {
        // Given
        final int pageSize = 5;
        final int lastPage = 3;
        final int totalOfItems = 13;

        final WebOptions theWebOptions = mockWebOptions( pageSize, lastPage );
        final List<DataItem> anyDimensionalItems = mockDimensionalItems( totalOfItems );

        // When
        final List<DataItem> resultingList = slice( theWebOptions,
            anyDimensionalItems );

        // Then
        assertThat( resultingList, hasSize( 3 ) );
    }

    @Test
    public void testSliceWhenPageSizeIsZero()
    {
        // Given
        final int pageSize = 0;
        final int lastPage = 3;
        final int totalOfItems = 13;

        final WebOptions theWebOptions = mockWebOptions( pageSize, lastPage );
        final List<DataItem> anyDimensionalItems = mockDimensionalItems( totalOfItems );

        // When
        assertThrows( "Page size must be greater than zero.", IllegalStateException.class,
            () -> slice( theWebOptions, anyDimensionalItems ) );
    }

    @Test
    public void testSliceWhenDimensionalItemListIsEmpty()
    {
        // Given
        final int pageSize = 5;
        final int lastPage = 3;

        final WebOptions theWebOptions = mockWebOptions( pageSize, lastPage );
        final List<DataItem> emptyDimensionalItems = emptyList();

        // When
        final List<DataItem> resultingList = slice( theWebOptions, emptyDimensionalItems );

        // Then
        assertThat( resultingList, is( emptyDimensionalItems ) );
        assertThat( resultingList, hasSize( 0 ) );
    }

    @Test
    public void testSliceWhenPageIsZero()
    {
        // Given
        final int pageSize = 5;
        final int currentPage = 0;

        final WebOptions theWebOptions = mockWebOptions( pageSize, currentPage );
        final List<DataItem> emptyDimensionalItems = emptyList();

        // When
        assertThrows( "Current page must be greater than zero.", IllegalStateException.class,
            () -> slice( theWebOptions, emptyDimensionalItems ) );
    }

    private WebOptions mockWebOptions( final int pageSize, final int pageNumber )
    {
        final Map<String, String> options = new HashMap<>( 0 );
        options.put( PAGE_SIZE, valueOf( pageSize ) );
        options.put( PAGE, valueOf( pageNumber ) );
        options.put( PAGING, "true" );

        return new WebOptions( options );
    }

    private List<DataItem> mockDimensionalItems(final int totalOfItems )
    {
        final List<DataItem> dataItemEntities = new ArrayList<>( 0 );

        for ( int i = 0; i < totalOfItems; i++ )
        {
            final DataItem dataItem = new DataItem();
            dataItem.setName( "d-" + i );
            dataItem.setId( "d-" + i );
            dataItemEntities.add(dataItem);
        }

        return dataItemEntities;
    }
}
