package org.hisp.dhis.webapi.mvc;



import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Component
public class CurrentUserHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver
{
    private final CurrentUserService currentUserService;

    public CurrentUserHandlerMethodArgumentResolver( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    @Override
    public boolean supportsParameter( MethodParameter parameter )
    {
        return "currentUser".equals( parameter.getParameterName() )
            && User.class.isAssignableFrom( parameter.getParameterType() );
    }

    @Override
    public Object resolveArgument( MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory ) throws Exception
    {
        return currentUserService.getCurrentUser();
    }
}
