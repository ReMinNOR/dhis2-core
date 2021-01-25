package org.hisp.dhis.result;



/**
* @author Lars Helge Overland
*/
public class GridPdfInlineResult
    extends GridPdfResult
{
    @Override    
    protected boolean isAttachment()
    {
        return false;
    }
}
