package org.hisp.dhis.webapi.mvc.interceptor;



import lombok.AllArgsConstructor;
import org.hisp.dhis.dxf2.common.TranslateParams;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserSettingService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

import static org.hisp.dhis.common.UserContext.reset;
import static org.hisp.dhis.common.UserContext.setUser;
import static org.hisp.dhis.common.UserContext.setUserSetting;
import static org.hisp.dhis.user.UserSettingKey.DB_LOCALE;

/**
 * This interceptor is ONLY responsible for setting the current user and its
 * related settings into the current request cycle/thread. The intention is to
 * leave it as simple as possible. Any business rules, if needed, should be
 * evaluated outside (in another interceptor or filter).
 *
 * @author maikel arabori
 */
@AllArgsConstructor
public class UserContextInterceptor extends HandlerInterceptorAdapter implements InitializingBean
{
    private static UserContextInterceptor instance;

    private static final String PARAM_TRANSLATE = "translate";

    private static final String PARAM_LOCALE = "locale";

    private final CurrentUserService currentUserService;

    private final UserSettingService userSettingService;

    public static UserContextInterceptor get()
    {
        return instance;
    }

    @Override
    public void afterPropertiesSet()
    {
        instance = this;
    }

    @Override
    public boolean preHandle( final HttpServletRequest request,
        final HttpServletResponse response, final Object handler )
        throws Exception
    {
        boolean translate = !"false".equals( request.getParameter( PARAM_TRANSLATE ) );

        String locale = request.getParameter( PARAM_LOCALE );

        User user = currentUserService.getCurrentUserInTransaction();

        if ( user != null )
        {
            configureUserContext( user, new TranslateParams( translate, locale ) );
        }

        return true;
    }

    @Override
    public void afterCompletion( final HttpServletRequest request, final HttpServletResponse response,
        final Object handler, final Exception ex )
    {
        reset();
    }

    private void configureUserContext( final User user, final TranslateParams translateParams )
    {
        final Locale dbLocale = getLocaleWithDefault( translateParams, user );

        setUser( user );
        setUserSetting( DB_LOCALE, dbLocale );
    }

    private Locale getLocaleWithDefault( final TranslateParams translateParams, final User user )
    {
        return translateParams.isTranslate()
            ? translateParams
            .getLocaleWithDefault( (Locale) userSettingService.getUserSetting( DB_LOCALE, user ) )
            : null;
    }
}
