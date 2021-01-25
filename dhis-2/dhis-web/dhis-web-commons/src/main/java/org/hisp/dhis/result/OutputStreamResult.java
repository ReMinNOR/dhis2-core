package org.hisp.dhis.result;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class OutputStreamResult
    implements Result
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6337406648858377663L;

    @Override
    public void execute( ActionInvocation arg0 )
        throws Exception
    {
    }    
}
