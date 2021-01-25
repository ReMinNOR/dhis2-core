package org.hisp.dhis.i18n.action;



import com.opensymphony.xwork2.Action;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;


import org.apache.struts2.ServletActionContext;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.translation.Translation;
import org.hisp.dhis.translation.TranslationProperty;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import static org.hisp.dhis.common.IdentifiableObjectUtils.CLASS_ALIAS;

/**
 * @author Oyvind Brucker
 * @author  Dang Duy Hieu
 */
@Slf4j
public class TranslateAction
    implements Action
{
    private String className;

    private String uid;

    private String loc;

    private String returnUrl;

    private String message;

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

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    public void setLoc( String locale )
    {
        this.loc = locale;
    }

    public void setReturnUrl( String returnUrl )
    {
        this.returnUrl = returnUrl;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    public String getClassName()
    {
        return className;
    }

    public String getUid()
    {
        return uid;
    }

    public String getLocale()
    {
        return loc;
    }

    public String getReturnUrl()
    {
        return returnUrl;
    }

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        className = className != null && CLASS_ALIAS.containsKey( className ) ? CLASS_ALIAS.get( className ) : className;
        
        log.info( "Classname: " + className + ", uid: " + uid + ", loc: " + loc );

        IdentifiableObject object = identifiableObjectManager.getObject( uid , className );

        HttpServletRequest request = ServletActionContext.getRequest();

        Set<Translation> listObjectTranslation = new HashSet<>(object.getTranslations());

        for ( TranslationProperty p :  TranslationProperty.values()  )
        {
            Enumeration<String> paramNames = request.getParameterNames();

            Collections.list( paramNames ).forEach( paramName -> {

                if ( paramName.equalsIgnoreCase( p.getName() ) )
                {
                    String[] paramValues = request.getParameterValues( paramName );

                    if ( !ArrayUtils.isEmpty( paramValues ) && StringUtils.isNotEmpty( paramValues[0]) )
                    {
                        listObjectTranslation.removeIf( o -> o.getProperty().equals( p ) && o.getLocale().equalsIgnoreCase( loc )  );

                        listObjectTranslation.add( new Translation( loc, p, paramValues[0] ) );
                    }
                }
            });
        }

        identifiableObjectManager.updateTranslations( object, listObjectTranslation );

        return SUCCESS;
    }
}
