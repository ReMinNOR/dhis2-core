package org.hisp.dhis.result;



import org.apache.struts2.result.VelocityResult;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class VelocityJsonResult
    extends VelocityResult
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1038408218156030639L;

    @Override
    protected final String getContentType( String templateLocation )
    {
        return "application/json";
    }
}
