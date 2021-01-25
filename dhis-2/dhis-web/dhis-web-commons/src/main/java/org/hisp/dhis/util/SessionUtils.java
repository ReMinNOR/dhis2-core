package org.hisp.dhis.util;



import com.opensymphony.xwork2.ActionContext;

/**
 * @author Lars Helge Overland
 * @version $Id$
 */
public class SessionUtils
{
    public static final String KEY_PREVIEW_TYPE = "previewType";
    public static final String KEY_PREVIEW_STATUS = "previewStatus";    
    public static final String KEY_CURRENT_YEAR = "currentYear";
    public static final String KEY_REPORT_TABLE_GRID = "lastReportTableGrid";
    public static final String KEY_REPORT_TABLE_PARAMS = "lastReportTableParams";
    public static final String KEY_DATASET_REPORT_GRID = "lastDataSetReportGrid";
    public static final String KEY_DATABROWSERGRID = "dataBrowserGridResults";
    public static final String KEY_SQLVIEW_GRID = "sqlViewGrid";
    
    public static Object getSessionVar( String name )
    {
        return ActionContext.getContext().getSession().get( name );
    }

    public static Object getSessionVar( String name, Object defaultValue )
    {
        Object object = ActionContext.getContext().getSession().get( name );
        
        return object != null ? object : defaultValue; 
    }

    public static void setSessionVar( String name, Object value )
    {
        ActionContext.getContext().getSession().put( name, value );
    }

    public static boolean containsSessionVar( String name )
    {
        return ActionContext.getContext().getSession().containsKey( name );
    }

    public static void removeSessionVar( String name )
    {
        ActionContext.getContext().getSession().remove( name );
    }
}
