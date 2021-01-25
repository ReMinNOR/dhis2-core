package org.hisp.dhis.webapi.view;



import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.webapi.utils.ContextUtils;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class PdfGridView extends AbstractGridView
{
    public PdfGridView()
    {
        setContentType( ContextUtils.CONTENT_TYPE_PDF );
    }

    @Override
    protected void renderGrids( List<Grid> grids, HttpServletResponse response ) throws Exception
    {
        GridUtils.toPdf( grids, response.getOutputStream() );
    }
}
