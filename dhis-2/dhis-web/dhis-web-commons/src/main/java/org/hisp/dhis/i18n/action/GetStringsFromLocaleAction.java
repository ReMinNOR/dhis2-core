package org.hisp.dhis.i18n.action;



import com.opensymphony.xwork2.Action;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.setting.TranslateSystemSettingManager;
import org.hisp.dhis.system.util.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

/**
 * @author Lars Helge Overland
 */
public class GetStringsFromLocaleAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------
    
    @Autowired
    private I18nManager manager;

    @Autowired
    private TranslateSystemSettingManager translateSystemSettingManager;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    private String loc;

    public void setLoc( String loc )
    {
        this.loc = loc;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private Map<String, String> translations = new Hashtable<>();

    public Map<String, String> getTranslations()
    {
        return translations;
    }

    private I18n i18nObject;

    public I18n getI18nObject()
    {
        return i18nObject;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        if ( loc != null )
        {
            Locale locale = LocaleUtils.getLocale( loc );
    
            i18nObject = manager.getI18n( this.getClass(), locale );
        
            translations = translateSystemSettingManager.getTranslationSystemAppearanceSettings( loc );
        }
        
        return SUCCESS;
    }
}
