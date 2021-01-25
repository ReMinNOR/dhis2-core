package org.hisp.dhis.webapi.handler;



import org.apache.commons.lang.exception.ExceptionUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CustomExceptionMappingAuthenticationFailureHandler
    extends ExceptionMappingAuthenticationFailureHandler
{
    private I18nManager i18nManager;

    public CustomExceptionMappingAuthenticationFailureHandler( I18nManager i18nManager )
    {
        this.i18nManager = i18nManager;
    }

    @Override
    public void onAuthenticationFailure(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException exception )
        throws IOException, ServletException
    {

        final String username = request.getParameter( "j_username" );

        request.getSession().setAttribute( "username", username );

        I18n i18n = i18nManager.getI18n();

        if ( ExceptionUtils.indexOfThrowable( exception, LockedException.class ) != -1 )
        {
            request.getSession()
                .setAttribute( "LOGIN_FAILED_MESSAGE", i18n.getString( "authentication.message.account.locked" ) );
        }
        else
        {
            request.getSession()
                .setAttribute( "LOGIN_FAILED_MESSAGE", i18n.getString( "authentication.message.account.invalid" ) );
        }

        super.onAuthenticationFailure( request, response, exception );
    }
}
