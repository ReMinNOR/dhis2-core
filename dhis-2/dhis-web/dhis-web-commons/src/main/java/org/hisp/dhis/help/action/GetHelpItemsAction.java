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
public class GetHelpItemsAction
    extends StreamActionSupport
{
    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    @Override
    protected String execute( HttpServletResponse response, OutputStream out )
        throws Exception
    {
        HelpManager.getHelpItems( out, localeManager.getCurrentLocale() );

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
