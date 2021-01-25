package org.hisp.dhis.webapi.webdomain;



import java.util.Map;

import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.common.Options;
import org.hisp.dhis.query.Junction;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class WebOptions
    extends Options
{
    public final static String PAGING = "paging";
    public final static String PAGE = "page";
    public final static String PAGE_SIZE = "pageSize";
    public final static String ROOT_JUNCTION = "rootJunction";
    public final static String VIEW_CLASS = "viewClass";
    public final static String MANAGE = "manage";

    public WebOptions( Map<String, String> options )
    {
        super( options );
    }

    //--------------------------------------------------------------------------
    // Getters for standard web options
    //--------------------------------------------------------------------------

    public boolean hasPaging()
    {
        return stringAsBoolean( options.get( PAGING ), true );
    }

    /**
     * This method will return a boolean flag depending on the current paging value
     * and the given default return value. The input param will be used to force the
     * return of this method in the cases where the PAGING is not set.
     * @param defaultReturnValue is the value to be returned if the paging is not
     *        set.
     * @return the boolean flag.
     */
    public boolean hasPaging( boolean defaultReturnValue )
    {
        return stringAsBoolean( options.get( PAGING ), defaultReturnValue );
    }

    public int getPage()
    {
        return stringAsInt( options.get( PAGE ), 1 );
    }

    public String getViewClass()
    {
        return stringAsString( options.get( VIEW_CLASS ), null );
    }

    public String getViewClass( String defaultValue )
    {
        return stringAsString( options.get( VIEW_CLASS ), defaultValue );
    }

    public int getPageSize()
    {
        return stringAsInt( options.get( PAGE_SIZE ), Pager.DEFAULT_PAGE_SIZE );
    }

    public boolean isManage()
    {
        return stringAsBoolean( options.get( MANAGE ), false );
    }

    public Junction.Type getRootJunction()
    {
        String rootJunction = options.get( ROOT_JUNCTION );
        return "OR".equals( rootJunction ) ? Junction.Type.OR : Junction.Type.AND;
    }
}
