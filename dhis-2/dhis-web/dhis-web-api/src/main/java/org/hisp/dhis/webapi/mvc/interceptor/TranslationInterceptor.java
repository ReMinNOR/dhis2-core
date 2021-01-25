package org.hisp.dhis.webapi.mvc.interceptor;



import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.UserContext;
import org.hisp.dhis.dxf2.common.TranslateParams;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserSettingKey;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * This was deprecated in favour of the new {@link UserContextInterceptor}. This
 * was disabled after xml to java config refactor, on (24.08.2020).
 * 
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
@Deprecated
public class TranslationInterceptor extends HandlerInterceptorAdapter implements InitializingBean
{
    private static TranslationInterceptor instance;

    @Override
    public void afterPropertiesSet()
        throws Exception
    {
        instance = this;
    }

    public static TranslationInterceptor get()
    {
        return instance;
    }

    private static String PARAM_TRANSLATE = "translate";

    private static String PARAM_LOCALE = "locale";

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserSettingService userSettingService;

    @Override
    public boolean preHandle( HttpServletRequest request, HttpServletResponse response, Object handler ) throws Exception
    {
        boolean translate = !"false".equals( request.getParameter( PARAM_TRANSLATE ) );
        String locale = request.getParameter( PARAM_LOCALE );

        User user = currentUserService.getCurrentUser();
        setUserContext( user, new TranslateParams( translate, locale ) );

        return true;
    }

    @Override
    public void afterCompletion( HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex )
    {
        UserContext.reset();
    }

    private void setUserContext( User user, TranslateParams translateParams )
    {
        Locale dbLocale = getLocaleWithDefault( translateParams, user );
        UserContext.setUser( user );
        UserContext.setUserSetting( UserSettingKey.DB_LOCALE, dbLocale );
    }

    private Locale getLocaleWithDefault( TranslateParams translateParams, User user )
    {
        return translateParams.isTranslate() ?
            translateParams.getLocaleWithDefault( (Locale) userSettingService.getUserSetting( UserSettingKey.DB_LOCALE, user ) ) : null;
    }
}
