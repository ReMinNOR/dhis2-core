package org.hisp.dhis.webapi.security.config;



import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.security.SecurityService;
import org.hisp.dhis.security.oidc.DhisOidcUser;
import org.hisp.dhis.security.spring2fa.TwoFactorWebAuthenticationDetails;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Henning Håkonsen
 */
@Slf4j
@Component
public class AuthenticationListener
{
    @Autowired
    private SecurityService securityService;

    @Autowired
    private UserService userService;

    @Autowired
    private DhisConfigurationProvider config;

    @EventListener
    public void handleAuthenticationFailure( AbstractAuthenticationFailureEvent event )
    {
        Authentication auth = event.getAuthentication();
        String username = event.getAuthentication().getName();

        if ( TwoFactorWebAuthenticationDetails.class.isAssignableFrom( auth.getDetails().getClass() ) )
        {
            TwoFactorWebAuthenticationDetails authDetails =
                (TwoFactorWebAuthenticationDetails) auth.getDetails();

            log.info( String.format( "Login attempt failed for remote IP: %s", authDetails.getIp() ) );
        }

        if ( OAuth2LoginAuthenticationToken.class.isAssignableFrom( auth.getClass() ) )
        {
            OAuth2LoginAuthenticationToken authenticationToken = (OAuth2LoginAuthenticationToken) auth;
            DhisOidcUser principal = (DhisOidcUser) authenticationToken.getPrincipal();

            if ( principal != null )
            {
                UserCredentials userCredentials = principal.getUserCredentials();
                username = userCredentials.getUsername();
            }

            WebAuthenticationDetails details = (WebAuthenticationDetails) authenticationToken.getDetails();
            String remoteAddress = details.getRemoteAddress();

            log.info( String.format( "OIDC login attempt failed for remote IP: %s", remoteAddress ) );
        }

        securityService.registerFailedLogin( username );
    }

    @EventListener( { InteractiveAuthenticationSuccessEvent.class, AuthenticationSuccessEvent.class } )
    public void handleAuthenticationSuccess( AbstractAuthenticationEvent event )
    {
        Authentication auth = event.getAuthentication();
        String username = event.getAuthentication().getName();

        if ( TwoFactorWebAuthenticationDetails.class.isAssignableFrom( auth.getDetails().getClass() ) )
        {
            TwoFactorWebAuthenticationDetails authDetails =
                (TwoFactorWebAuthenticationDetails) auth.getDetails();

            log.debug( String.format( "Login attempt succeeded for remote IP: %s", authDetails.getIp() ) );
        }

        if ( OAuth2LoginAuthenticationToken.class.isAssignableFrom( auth.getClass() ) )
        {
            OAuth2LoginAuthenticationToken authenticationToken = (OAuth2LoginAuthenticationToken) auth;
            DhisOidcUser principal = (DhisOidcUser) authenticationToken.getPrincipal();
            UserCredentials userCredentials = principal.getUserCredentials();
            username = userCredentials.getUsername();

            WebAuthenticationDetails details = (WebAuthenticationDetails) authenticationToken.getDetails();
            String remoteAddress = details.getRemoteAddress();

            log.debug( String.format( "OIDC login attempt succeeded for remote IP: %s", remoteAddress ) );
        }

        registerSuccessfulLogin( username );
    }

    private void registerSuccessfulLogin( String username )
    {
        UserCredentials credentials = userService.getUserCredentialsByUsername( username );

        boolean readOnly = config.isReadOnlyMode();

        if ( Objects.nonNull( credentials ) && !readOnly )
        {
            credentials.updateLastLogin();
            userService.updateUserCredentials( credentials );
        }

        securityService.registerSuccessfulLogin( username );
    }
}
