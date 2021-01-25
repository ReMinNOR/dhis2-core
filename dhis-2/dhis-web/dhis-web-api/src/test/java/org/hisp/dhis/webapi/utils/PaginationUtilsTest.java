package org.hisp.dhis.webapi.utils;



import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.query.Pagination;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.junit.Test;

/**
 * @author Luciano Fiandesio
 */
public class PaginationUtilsTest
{
    @Test
    public void verifyPaginationStartsAtZero()
    {
        Map<String, String> options = new HashMap<>();
        options.put( WebOptions.PAGING, "true" );
        options.put( WebOptions.PAGE, "1" );
        options.put( WebOptions.PAGE_SIZE, "20" );
        WebOptions webOptions = new WebOptions( options );

        Pagination paginationData = PaginationUtils.getPaginationData( webOptions );

        assertThat( paginationData.getFirstResult(), is( 0 ) );
        assertThat( paginationData.getSize(), is( 20 ) );

    }

    @Test
    public void verifyPaginationCalculation()
    {
        Map<String, String> options = new HashMap<>();
        options.put( WebOptions.PAGING, "true" );
        options.put( WebOptions.PAGE, "14" );
        options.put( WebOptions.PAGE_SIZE, "200" );
        WebOptions webOptions = new WebOptions( options );

        Pagination paginationData = PaginationUtils.getPaginationData( webOptions );

        assertThat( paginationData.getFirstResult(), is( 2600 ) );
        assertThat( paginationData.getSize(), is( 200 ) );
    }

    @Test
    public void verifyPaginationIsDisabled()
    {
        Map<String, String> options = new HashMap<>();
        options.put( WebOptions.PAGING, "false" );
        WebOptions webOptions = new WebOptions( options );

        Pagination paginationData = PaginationUtils.getPaginationData( webOptions );

        assertThat( paginationData.getFirstResult(), is( 0 ) );
        assertThat( paginationData.getSize(), is( 0 ) );
        assertThat( paginationData.hasPagination(), is( false ) );
    }

    @Test
    public void verifyIgnoreNegativePage()
    {
        Map<String, String> options = new HashMap<>();
        options.put( WebOptions.PAGING, "true" );
        options.put( WebOptions.PAGE, "-2" );
        options.put( WebOptions.PAGE_SIZE, "200" );
        WebOptions webOptions = new WebOptions( options );

        Pagination paginationData = PaginationUtils.getPaginationData( webOptions );

        assertThat( paginationData.getFirstResult(), is( 0 ) );
        assertThat( paginationData.getSize(), is( 200 ) );
    }

}
