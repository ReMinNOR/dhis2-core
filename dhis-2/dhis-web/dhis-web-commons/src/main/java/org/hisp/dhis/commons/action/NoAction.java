package org.hisp.dhis.commons.action;



import com.opensymphony.xwork2.Action;

/**
 * @author Lars Helge Overland
 */
public class NoAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
    {
        return SUCCESS;
    }

}
