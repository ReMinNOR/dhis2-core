package org.hisp.dhis.result;



import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;
import com.opensymphony.xwork2.util.TextParseUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.struts2.StrutsStatics;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: PlainTextErrorResult.java 2869 2007-02-20 14:26:09Z andegje $
 */
public class PlainTextErrorResult
    implements Result
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 5500263448725254319L;

    // -------------------------------------------------------------------------
    // Parameters
    // -------------------------------------------------------------------------

    private boolean parse = true;

    public void setParse( boolean parse )
    {
        this.parse = parse;
    }

    private String message;

    public void setMessage( String message )
    {
        this.message = message;
    }

    // -------------------------------------------------------------------------
    // Result implementation
    // -------------------------------------------------------------------------

    @Override
    public void execute( ActionInvocation invocation )
        throws Exception
    {
        HttpServletResponse response = (HttpServletResponse) invocation.getInvocationContext().get(
            StrutsStatics.HTTP_RESPONSE );

        response.setContentType( "text/plain; charset=UTF-8" );
        response.setHeader( "Content-Disposition", "inline" );
        response.setStatus( HttpServletResponse.SC_INTERNAL_SERVER_ERROR );

        ValueStack stack = ActionContext.getContext().getValueStack();
        String finalMessage = parse ? TextParseUtil.translateVariables( message, stack ) : message;

        finalMessage = formatFinalMessage( finalMessage );

        // ---------------------------------------------------------------------
        // Write final message
        // ---------------------------------------------------------------------

        PrintWriter writer = null;

        try
        {
            writer = response.getWriter();
            writer.print( finalMessage );
            writer.flush();
        }
        finally
        {
            if ( writer != null )
            {
                writer.close();
            }
        }
    }

    /**
     * Remove the first colon character ( : ) if the class name does not present in the message
     * @param message with format ${exception.class.name}: ${exception.message}
     * @return formated message
     */
    private String formatFinalMessage( String message )
    {
        if ( message.startsWith( ":" ) )
        {
            return message.substring( 1, message.length() );
        }

        return message;
    }
}
