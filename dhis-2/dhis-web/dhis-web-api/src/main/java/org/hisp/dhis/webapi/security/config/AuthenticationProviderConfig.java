package org.hisp.dhis.webapi.security.config;



import com.google.common.collect.ImmutableMap;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.security.AuthenticationLoggerListener;
import org.hisp.dhis.security.ldap.authentication.CustomLdapAuthenticationProvider;
import org.hisp.dhis.security.ldap.authentication.DhisBindAuthenticator;
import org.hisp.dhis.security.oauth2.DefaultClientDetailsUserDetailsService;
import org.hisp.dhis.security.spring2fa.TwoFactorAuthenticationProvider;
import org.hisp.dhis.security.spring2fa.TwoFactorWebAuthenticationDetailsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.UserDetailsServiceLdapAuthoritiesPopulator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

/**
 * @author Morten Svanæs <msvanaes@dhis2.org>
 */
@Configuration
@Order( 910 )
@ComponentScan( basePackages = { "org.hisp.dhis" } )
@EnableWebSecurity
public class AuthenticationProviderConfig
{
    @Autowired
    private DhisConfigurationProvider configurationProvider;

    @Autowired
    TwoFactorAuthenticationProvider twoFactorAuthenticationProvider;

    @Autowired
    DefaultClientDetailsUserDetailsService defaultClientDetailsUserDetailsService;

    @Bean
    public TwoFactorWebAuthenticationDetailsSource twoFactorWebAuthenticationDetailsSource()
    {
        return new TwoFactorWebAuthenticationDetailsSource();
    }

    @Bean( name = "customLdapAuthenticationProvider" )
    CustomLdapAuthenticationProvider customLdapAuthenticationProvider()
    {
        return new CustomLdapAuthenticationProvider( dhisBindAuthenticator(),
            userDetailsServiceLdapAuthoritiesPopulator( defaultClientDetailsUserDetailsService ),
            configurationProvider );
    }

    @Bean
    public DefaultSpringSecurityContextSource defaultSpringSecurityContextSource()
    {
        DefaultSpringSecurityContextSource defaultSpringSecurityContextSource = new DefaultSpringSecurityContextSource(
            configurationProvider.getProperty( ConfigurationKey.LDAP_URL ) );
        defaultSpringSecurityContextSource
            .setUserDn( configurationProvider.getProperty( ConfigurationKey.LDAP_MANAGER_DN ) );
        defaultSpringSecurityContextSource
            .setPassword( configurationProvider.getProperty( ConfigurationKey.LDAP_MANAGER_PASSWORD ) );

        return defaultSpringSecurityContextSource;
    }

    @Bean
    public FilterBasedLdapUserSearch filterBasedLdapUserSearch()
    {
        return new FilterBasedLdapUserSearch( configurationProvider.getProperty( ConfigurationKey.LDAP_SEARCH_BASE ),
            configurationProvider.getProperty( ConfigurationKey.LDAP_SEARCH_FILTER ),
            defaultSpringSecurityContextSource() );
    }

    @Bean
    @DependsOn( "org.hisp.dhis.user.UserService" )
    public DhisBindAuthenticator dhisBindAuthenticator()
    {
        DhisBindAuthenticator dhisBindAuthenticator = new DhisBindAuthenticator( defaultSpringSecurityContextSource() );
        dhisBindAuthenticator.setUserSearch( filterBasedLdapUserSearch() );
        return dhisBindAuthenticator;
    }

    @Bean
    public UserDetailsServiceLdapAuthoritiesPopulator userDetailsServiceLdapAuthoritiesPopulator(
        UserDetailsService userDetailsService )
    {
        return new UserDetailsServiceLdapAuthoritiesPopulator( userDetailsService );
    }

    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher()
    {
        DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher = new DefaultAuthenticationEventPublisher();
        defaultAuthenticationEventPublisher.setAdditionalExceptionMappings(
            ImmutableMap.of( OAuth2AuthenticationException.class, AuthenticationFailureBadCredentialsEvent.class ) );
        return defaultAuthenticationEventPublisher;
    }

    @Bean
    public AuthenticationLoggerListener authenticationLoggerListener()
    {
        return new AuthenticationLoggerListener();
    }

    @Bean
    public AuthenticationListener authenticationListener()
    {
        return new AuthenticationListener();
    }
}
