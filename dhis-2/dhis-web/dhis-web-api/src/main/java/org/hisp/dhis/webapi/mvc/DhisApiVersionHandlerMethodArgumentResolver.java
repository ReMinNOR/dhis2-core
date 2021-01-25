package org.hisp.dhis.webapi.mvc;



import org.hisp.dhis.common.DhisApiVersion;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class DhisApiVersionHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private Pattern API_VERSION_PATTERN = Pattern.compile( "/api/(?<version>[0-9]{2})/" );

    @Override
    public boolean supportsParameter( MethodParameter parameter )
    {
        return DhisApiVersion.class.isAssignableFrom( parameter.getParameterType() );
    }

    @Override
    public Object resolveArgument( MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory ) throws Exception
    {
        String requestURI = ((HttpServletRequest) webRequest.getNativeRequest()).getRequestURI();
        Matcher matcher = API_VERSION_PATTERN.matcher( requestURI );

        if ( matcher.find() )
        {
            Integer version = Integer.valueOf( matcher.group( "version" ) );
            return DhisApiVersion.getVersion( version );
        }

        return DhisApiVersion.DEFAULT;
    }
}
