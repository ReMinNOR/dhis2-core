package org.hisp.dhis.help.action;



import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.i18n.locale.LocaleManager;
import org.hisp.dhis.system.help.HelpManager;
import org.hisp.dhis.util.ContextUtils;
import org.hisp.dhis.util.StreamActionSupport;

/**
 * @author Lars Helge Overland
 */
public class GetHelpContentAction
    extends StreamActionSupport
{
    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    private String id;

    public void setId( String id )
    {
        this.id = id;
    }

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        HelpManager.getHelpContent( out, id, localeManager.getCurrentLocale() );

        return SUCCESS;
    }

    @Override
    protected String getContentType()
    {
        return ContextUtils.CONTENT_TYPE_HTML;
    }

    @Override
    protected String getFilename()
    {
        return "help.html";
    }
}
