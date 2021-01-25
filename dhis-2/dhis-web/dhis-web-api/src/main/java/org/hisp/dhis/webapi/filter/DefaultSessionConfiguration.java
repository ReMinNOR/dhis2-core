package org.hisp.dhis.webapi.filter;


import javax.servlet.Filter;

import org.hisp.dhis.condition.RedisDisabledCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.filter.CharacterEncodingFilter;

/**
 * Configuration registered if {@link RedisDisabledCondition} matches to true.
 * This serves as a fallback to spring-session if redis is disabled. Since
 * web.xml has a "springSessionRepositoryFilter" mapped to all urls, the
 * container will expect a filter bean with that name. Therefore we define a
 * dummy {@link Filter} named springSessionRepositoryFilter. Here we define a
 * {@link CharacterEncodingFilter} without setting any encoding so that requests
 * will simply pass through the filter.
 *
 * @author Ameen Mohamed
 *
 */
@Configuration
@DependsOn("dhisConfigurationProvider")
@Conditional( RedisDisabledCondition.class )
public class DefaultSessionConfiguration
{
    /**
     * Defines a {@link CharacterEncodingFilter} named
     * springSessionRepositoryFilter
     *
     * @return a {@link CharacterEncodingFilter} without specifying encoding.
     */
    @Bean
    public Filter springSessionRepositoryFilter()
    {
        return new CharacterEncodingFilter();
    }
}
