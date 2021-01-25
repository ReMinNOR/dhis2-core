package org.hisp.dhis.webapi;



import com.google.common.collect.ImmutableMap;
import org.hisp.dhis.config.H2DhisConfigurationProvider;
import org.hisp.dhis.config.DataSourceConfig;
import org.hisp.dhis.config.HibernateEncryptionConfig;
import org.hisp.dhis.config.HibernateConfig;
import org.hisp.dhis.config.ServiceConfig;
import org.hisp.dhis.config.StartupConfig;
import org.hisp.dhis.config.StoreConfig;
import org.hisp.dhis.configuration.NotifierConfiguration;
import org.hisp.dhis.db.migration.config.FlywayConfig;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.jdbc.config.JdbcConfig;
import org.hisp.dhis.leader.election.LeaderElectionConfiguration;
import org.hisp.dhis.security.config.DhisWebCommonsWebSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
/**
 * @author Gintare Vilkelyte <vilkelyte.gintare@gmail.com
 */
@Configuration
@ImportResource( locations = { "classpath*:/META-INF/dhis/beans.xml" } )
@ComponentScan( basePackages = { "org.hisp.dhis" }, useDefaultFilters = false, includeFilters = {
    @Filter( type = FilterType.ANNOTATION, value = Service.class ),
    @Filter( type = FilterType.ANNOTATION, value = Component.class ),
    @Filter( type = FilterType.ANNOTATION, value = Repository.class )

}, excludeFilters = @Filter( Configuration.class ) )
@Import( {
    HibernateConfig.class,
    DataSourceConfig.class,
    JdbcConfig.class,
    FlywayConfig.class,
    HibernateEncryptionConfig.class,
    ServiceConfig.class,
    StoreConfig.class,
    LeaderElectionConfiguration.class,
    NotifierConfiguration.class,
    DhisWebCommonsWebSecurityConfig.class,
    org.hisp.dhis.setting.config.ServiceConfig.class,
    org.hisp.dhis.external.config.ServiceConfig.class,
    org.hisp.dhis.dxf2.config.ServiceConfig.class,
    org.hisp.dhis.support.config.ServiceConfig.class,
    org.hisp.dhis.validation.config.ServiceConfig.class,
    org.hisp.dhis.validation.config.StoreConfig.class,
    org.hisp.dhis.programrule.config.ProgramRuleConfig.class,
    org.hisp.dhis.reporting.config.StoreConfig.class,
    org.hisp.dhis.analytics.config.ServiceConfig.class,
    org.hisp.dhis.commons.config.JacksonObjectMapperConfig.class,
    StartupConfig.class
} )
@Transactional
public class WebTestConfiguration
{
    @Bean( name = "dhisConfigurationProvider" )
    public DhisConfigurationProvider dhisConfigurationProvider()
    {
        return new H2DhisConfigurationProvider();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public LdapAuthenticator ldapAuthenticator()
    {
        return authentication -> null;
    }

    @Bean
    public LdapAuthoritiesPopulator ldapAuthoritiesPopulator()
    {
        return ( dirContextOperations, s ) -> null;
    }

    @Bean( "oAuth2AuthenticationManager" )
    public AuthenticationManager oAuth2AuthenticationManager()
    {
        return authentication -> null;
    }

    @Bean( "authenticationManager" )
    @Primary
    public AuthenticationManager authenticationManager()
    {
        return authentication -> null;
    }

    @Bean
    public DefaultAuthenticationEventPublisher authenticationEventPublisher()
    {
        DefaultAuthenticationEventPublisher defaultAuthenticationEventPublisher = new DefaultAuthenticationEventPublisher();
        defaultAuthenticationEventPublisher.setAdditionalExceptionMappings(
            ImmutableMap.of( OAuth2AuthenticationException.class, AuthenticationFailureBadCredentialsEvent.class ) );
        return defaultAuthenticationEventPublisher;
    }
}
