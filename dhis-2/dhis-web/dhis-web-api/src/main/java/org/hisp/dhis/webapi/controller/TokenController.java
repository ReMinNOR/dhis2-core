package org.hisp.dhis.webapi.controller;



import static org.hisp.dhis.webapi.utils.ContextUtils.setNoStore;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.cache.Cache;
import org.hisp.dhis.cache.CacheProvider;
import org.hisp.dhis.common.DhisApiVersion;
import org.hisp.dhis.commons.util.SystemUtils;
import org.hisp.dhis.dxf2.webmessage.WebMessageException;
import org.hisp.dhis.dxf2.webmessage.WebMessageUtils;
import org.hisp.dhis.external.conf.DhisConfigurationProvider;
import org.hisp.dhis.external.conf.GoogleAccessToken;
import org.hisp.dhis.webapi.mvc.annotation.ApiVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = TokenController.RESOURCE_PATH )
@ApiVersion( { DhisApiVersion.DEFAULT, DhisApiVersion.ALL } )
public class TokenController
{
    public static final String RESOURCE_PATH = "/tokens";

    private static final String TOKEN_CACHE_KEY = "keyGoogleAccessToken";

    @Autowired
    private CacheProvider cacheProvider;

    @Autowired
    private Environment environment;

    private Cache<GoogleAccessToken> TOKEN_CACHE;

    @PostConstruct
    public void init()
    {
        TOKEN_CACHE = cacheProvider.newCacheBuilder( GoogleAccessToken.class ).forRegion( "googleAccessToken" )
            .expireAfterAccess( 10, TimeUnit.MINUTES )
            .withMaximumSize( SystemUtils.isTestRun( environment.getActiveProfiles() ) ? 0 : 1 ).build();
    }

    @Autowired
    private DhisConfigurationProvider config;

    @RequestMapping( value = "/google", method = RequestMethod.GET, produces = "application/json" )
    public @ResponseBody GoogleAccessToken getEarthEngineToken( HttpServletResponse response )
        throws WebMessageException, ExecutionException
    {
        setNoStore( response );

        Optional<GoogleAccessToken> tokenOptional = TOKEN_CACHE.get( TOKEN_CACHE_KEY, c -> config.getGoogleAccessToken().get() );

        if ( !tokenOptional.isPresent() )
        {
            throw new WebMessageException( WebMessageUtils.conflict( "Token not available" ) );
        }

        GoogleAccessToken token = tokenOptional.get();

        token.setExpiresInSeconds( ChronoUnit.SECONDS.between( LocalDateTime.now(), token.getExpiresOn() ) );

        return token;
    }
}
