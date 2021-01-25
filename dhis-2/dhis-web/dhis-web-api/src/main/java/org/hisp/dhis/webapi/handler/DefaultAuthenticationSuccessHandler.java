package org.hisp.dhis.webapi.handler;





import lombok.extern.slf4j.Slf4j;
import org.hisp.dhis.external.conf.ConfigurationKey;
import org.hisp.dhis.util.ObjectUtils;
import org.joda.time.DateTimeConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Since ActionContext is not available at this point, we set a mark in the
 * session that signals that login has just occurred, and that LoginInterceptor
 * should be run.
 *
 * @author mortenoh
 */
@Slf4j
public class DefaultAuthenticationSuccessHandler
    extends SavedRequestAwareAuthenticationSuccessHandler
{
    public static final String JLI_SESSION_VARIABLE = "JLI";

    private static final int SESSION_MIN = DateTimeConstants.SECONDS_PER_MINUTE * 10;
    private static final int SESSION_DEFAULT = Integer.parseInt( ConfigurationKey.SYSTEM_SESSION_TIMEOUT.getDefaultValue() ); // 3600 s
    private static final String SESSION_MIN_MSG = "Session timeout must be greater than %d seconds";
    private static final String SESSION_INFO_MSG = "Session timeout set to %d seconds";
    
    private int systemSessionTimeout;
    
    /**
     * Configurable session timeout.
     */
    private Integer sessionTimeout;
    
    public void setSessionTimeout( Integer sessionTimeout )
    {
        this.sessionTimeout = sessionTimeout;
    }
    
    @PostConstruct
    public void init()
    {
        systemSessionTimeout = ObjectUtils.firstNonNull( sessionTimeout, SESSION_DEFAULT );
        
        Assert.isTrue( systemSessionTimeout >= SESSION_MIN, String.format( SESSION_MIN_MSG, SESSION_MIN ) );
        
        log.info( String.format( SESSION_INFO_MSG, systemSessionTimeout ) );
    }

    @Override
    public void onAuthenticationSuccess( HttpServletRequest request, HttpServletResponse response, Authentication authentication )
        throws ServletException, IOException
    {   
        HttpSession session = request.getSession();
        
        final String username = authentication.getName();
        
        session.setAttribute( "userIs", username );
        session.setAttribute( JLI_SESSION_VARIABLE, Boolean.TRUE );
        session.setMaxInactiveInterval( systemSessionTimeout );
        
        super.onAuthenticationSuccess( request, response, authentication );
    }
}
