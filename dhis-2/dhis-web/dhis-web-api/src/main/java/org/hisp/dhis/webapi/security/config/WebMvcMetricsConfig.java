package org.hisp.dhis.webapi.security.config;



import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.spring.web.servlet.DefaultWebMvcTagsProvider;
import io.micrometer.spring.web.servlet.WebMvcMetricsFilter;
import io.micrometer.spring.web.servlet.WebMvcTagsProvider;
import org.hisp.dhis.condition.PropertiesAwareConfigurationCondition;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.monitoring.metrics.MetricsEnabler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.hisp.dhis.external.conf.ConfigurationKey.MONITORING_API_ENABLED;

/**
 * @author Luciano Fiandesio
 */
@Configuration
@Conditional( WebMvcMetricsConfig.WebMvcMetricsEnabledCondition.class )
public class WebMvcMetricsConfig
{
    @Bean
    public DefaultWebMvcTagsProvider servletTagsProvider()
    {
        return new DefaultWebMvcTagsProvider();
    }

    @Bean
    public WebMvcMetricsFilter webMetricsFilter( MeterRegistry registry, WebMvcTagsProvider tagsProvider,
        HandlerMappingIntrospector handlerMappingIntrospector )
    {
        return new WebMvcMetricsFilter( registry, tagsProvider, "http_server_requests", true,
            handlerMappingIntrospector );
    }

    @Configuration
    @Conditional( WebMvcMetricsDisabledCondition.class )
    static class DataSourcePoolMetadataMetricsConfiguration
    {
        @Bean
        public PassThroughWebMvcMetricsFilter webMetricsFilter()
        {
            return new PassThroughWebMvcMetricsFilter();
        }
    }

    // If API metrics are disabled, system still expects a filter named 'webMetricsFilter' to be available

    static class PassThroughWebMvcMetricsFilter
        extends OncePerRequestFilter
    {
        @Override
        protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain )
            throws ServletException,
            IOException
        {
            filterChain.doFilter( request, response );
        }
    }

    static class WebMvcMetricsEnabledCondition
            extends MetricsEnabler
    {
        @Override
        protected ConfigurationKey getConfigKey()
        {
            return MONITORING_API_ENABLED;
        }
    }

    static class WebMvcMetricsDisabledCondition
        extends PropertiesAwareConfigurationCondition
    {
        @Override
        public ConfigurationPhase getConfigurationPhase()
        {
            return ConfigurationPhase.REGISTER_BEAN;
        }

        @Override
        public boolean matches( ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata )
        {
            return isTestRun( conditionContext ) || !getBooleanValue( MONITORING_API_ENABLED );
        }
    }
}
