package org.hisp.dhis.webapi.view;



import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.webapi.utils.ContextUtils;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CsvGridView extends AbstractGridView
{
    public CsvGridView()
    {
        setContentType( ContextUtils.CONTENT_TYPE_CSV );
    }

    @Override
    protected void renderGrids( List<Grid> grids, HttpServletResponse response ) throws Exception
    {
        if ( !grids.isEmpty() )
        {
            GridUtils.toCsv( grids.get( 0 ), response.getWriter() );
        }
    }
}
