package org.hisp.dhis.result;



import org.apache.struts2.result.VelocityResult;

public class VelocityJavascriptResult
    extends VelocityResult
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 1038408265456030639L;

    @Override
    protected final String getContentType( String templateLocation )
    {
        return "application/javascript";
    }
}
