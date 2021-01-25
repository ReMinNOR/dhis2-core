package org.hisp.dhis.about.action;



import java.util.List;
import java.util.Locale;

import org.hisp.dhis.i18n.locale.LocaleManager;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 * @version $Id: HelpAction.java 3116 2007-03-20 12:04:19Z torgeilo $
 */
public class HelpAction
    implements Action
{
    private static final String helpPagePreLocale = "/dhis-web-commons/help/help_";

    private static final String helpPagePostLocale = ".vm";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LocaleManager localeManager;

    public void setLocaleManager( LocaleManager localeManager )
    {
        this.localeManager = localeManager;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private String helpPage;

    public String getHelpPage()
    {
        return helpPage;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        List<Locale> locales = localeManager.getLocalesOrderedByPriority();

        ResourceLoader resourceLoader = new DefaultResourceLoader();

        for ( Locale locale : locales )
        {
            String helpPage = helpPagePreLocale + locale.toString() + helpPagePostLocale;

            if ( resourceLoader.getResource( helpPage ) != null )
            {
                this.helpPage = helpPage;

                return SUCCESS;
            }
        }

        return SUCCESS;
    }
}
