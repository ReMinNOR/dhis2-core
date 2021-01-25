package org.hisp.dhis.result;



import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * @author Lars Helge Overland
 */
public class GridCsvResult
    implements Result
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -8568665099535892269L;

    private static final String DEFAULT_FILENAME = "Grid.csv";

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Grid grid;
    
    public void setGrid( Grid grid )
    {
        this.grid = grid;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get grid
        // ---------------------------------------------------------------------

        Grid _grid = (Grid) invocation.getStack().findValue( "grid" );
        
        grid = _grid != null ? _grid : grid; 

        // ---------------------------------------------------------------------
        // Configure response
        // ---------------------------------------------------------------------

        HttpServletResponse response = ServletActionContext.getResponse();

        String filename = CodecUtils.filenameEncode( StringUtils.defaultIfEmpty( grid.getTitle(), DEFAULT_FILENAME ) ) + ".csv";
        
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_CSV, true, filename, true );

        // ---------------------------------------------------------------------
        // Write CSV to output stream
        // ---------------------------------------------------------------------

        GridUtils.toCsv( grid, response.getWriter() );
    }
}
