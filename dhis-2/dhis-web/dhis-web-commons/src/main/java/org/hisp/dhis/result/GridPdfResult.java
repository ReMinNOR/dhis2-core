package org.hisp.dhis.result;



import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static org.hisp.dhis.system.util.CodecUtils.filenameEncode;

import java.io.OutputStream;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * Creates a PDF representation of the given Grid or list of Grids and writes
 * it to the servlet outputstream. One of the grid or grids arguments must be set.
 * 
 * @author Lars Helge Overland
 */
public class GridPdfResult
    implements Result
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6613101138470779866L;

    private static final String DEFAULT_NAME = "Grid";

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Grid grid;

    public void setGrid( Grid grid )
    {
        this.grid = grid;
    }

    private List<Grid> grids;

    public void setGrids( List<Grid> grids )
    {
        this.grids = grids;
    }
   
    private boolean attachment = true;
    
    protected boolean isAttachment()
    {
        return attachment;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings( "unchecked" )
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get grid
        // ---------------------------------------------------------------------

        Grid _grid = (Grid) invocation.getStack().findValue( "grid" );

        grid = _grid != null ? _grid : grid;

        List<Grid> _grids = (List<Grid>) invocation.getStack().findValue( "grids" );
        
        grids = _grids != null ? _grids : grids;
        
        // ---------------------------------------------------------------------
        // Configure response
        // ---------------------------------------------------------------------

        HttpServletResponse response = ServletActionContext.getResponse();

        OutputStream out = response.getOutputStream();

        String filename = filenameEncode( defaultIfEmpty( grid != null ? grid.getTitle() : grids.iterator().next().getTitle(), DEFAULT_NAME ) ) + ".pdf";
        
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_PDF, true, filename, isAttachment() );

        // ---------------------------------------------------------------------
        // Write PDF to output stream
        // ---------------------------------------------------------------------

        if ( grid != null )
        {
            GridUtils.toPdf( grid, out );
        }
        else
        {
            GridUtils.toPdf( grids, out );
        }
    }
}
