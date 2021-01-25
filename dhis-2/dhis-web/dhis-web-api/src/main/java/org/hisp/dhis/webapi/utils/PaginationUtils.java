package org.hisp.dhis.webapi.utils;



import org.hisp.dhis.query.Pagination;
import org.hisp.dhis.webapi.webdomain.WebOptions;

/**
 * @author Luciano Fiandesio
 */
public class PaginationUtils
{
    public final static Pagination NO_PAGINATION = new Pagination();

    /**
     * Calculates the paging first result based on pagination data from
     * {@see WebOptions} if the WebOptions have pagination information
     * 
     * The first result is simply calculated by multiplying page -1 * page size
     * 
     * @param options a {@see WebOptions} object
     * @return a {@see PaginationData} object either empty or containing pagination
     *         data
     */
    public static Pagination getPaginationData( WebOptions options )
    {
        if ( options.hasPaging() )
        {
            // ignore if page < 0
            int page = Math.max( options.getPage(), 1 );
            return new Pagination( (page - 1) * options.getPageSize(), options.getPageSize() );
        }

        return NO_PAGINATION;
    }
}
