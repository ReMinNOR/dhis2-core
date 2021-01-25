package org.hisp.dhis.i18n.action;



import com.opensymphony.xwork2.ActionSupport;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.system.util.LocaleUtils;
import org.hisp.dhis.util.TranslationUtils;

import java.util.Locale;
import java.util.Map;

import static org.hisp.dhis.common.IdentifiableObjectUtils.CLASS_ALIAS;

/**
 * @author Oyvind Brucker
 */
public class GetTranslationsAction 
    extends ActionSupport
{
    private String className;

    private String objectUid;

    private String loc;

    private Map<String, String> translations;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IdentifiableObjectManager identifiableObjectManager;

    public void setIdentifiableObjectManager( IdentifiableObjectManager identifiableObjectManager )
    {
        this.identifiableObjectManager = identifiableObjectManager;
    }
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    public void setClassName( String className )
    {
        this.className = className;
    }

    public void setObjectUid( String objectUid )
    {
        this.objectUid = objectUid;
    }

    public void setLoc( String locale )
    {
        this.loc = locale;
    }

    public Map<String, String> getTranslations()
    {
        return translations;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        className = className != null && CLASS_ALIAS.containsKey( className ) ? CLASS_ALIAS.get( className ) : className;
        
        Locale locale = LocaleUtils.getLocale( loc );

        IdentifiableObject object = identifiableObjectManager.getObject( objectUid , className );

        translations = TranslationUtils.convertTranslations( object.getTranslations(), locale );

        return SUCCESS;
    }
}

