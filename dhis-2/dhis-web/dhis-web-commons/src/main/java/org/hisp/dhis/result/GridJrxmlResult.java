package org.hisp.dhis.result;



import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.Grid;
import org.hisp.dhis.system.grid.GridUtils;
import org.hisp.dhis.system.util.CodecUtils;
import org.hisp.dhis.util.ContextUtils;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * @author Lars Helge Overland
 */
public class GridJrxmlResult
    implements Result
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -2883847942469564557L;

    private static final String DEFAULT_FILENAME = "Grid";

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Grid grid;
    
    public void setGrid( Grid grid )
    {
        this.grid = grid;
    }

    private Map<Object, Object> params;

    public void setParams( Map<Object, Object> params )
    {
        this.params = params;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Get grid
        // ---------------------------------------------------------------------

        Grid _grid = (Grid) invocation.getStack().findValue( "grid" );
        
        grid = _grid != null ? _grid : grid; 

        Map<Object, Object> _params = (Map<Object, Object>) invocation.getStack().findValue( "params" );

        params = _params != null ? _params : params;
        
        // ---------------------------------------------------------------------
        // Configure response
        // ---------------------------------------------------------------------

        HttpServletResponse response = ServletActionContext.getResponse();
        
        Writer writer = response.getWriter();

        String filename = CodecUtils.filenameEncode( StringUtils.defaultIfEmpty( grid.getTitle(), DEFAULT_FILENAME ) ) + ".jrxml";
        
        ContextUtils.configureResponse( response, ContextUtils.CONTENT_TYPE_XML, true, filename, true );
        
        // ---------------------------------------------------------------------
        // Write jrxml based on Velocity template
        // ---------------------------------------------------------------------

        GridUtils.toJrxml( grid, params, writer );
    }
}
