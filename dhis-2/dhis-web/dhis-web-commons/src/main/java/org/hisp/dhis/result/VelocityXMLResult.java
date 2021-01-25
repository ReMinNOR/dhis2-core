package org.hisp.dhis.result;



import org.apache.struts2.result.VelocityResult;

/**
 * @author Hans S. Toemmerholt
 * @version $Id$
 */
public class VelocityXMLResult
    extends VelocityResult
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 3813189797060697257L;

    @Override
    protected final String getContentType( String templateLocation )
    {
        return "text/xml";
    }
}
