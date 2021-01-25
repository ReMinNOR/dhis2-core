package org.hisp.dhis.security;



import com.opensymphony.xwork2.config.entities.ActionConfig;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class StrutsAuthorityUtils
{
    public static Collection<String> getAuthorities( ActionConfig actionConfig, String key )
    {
        final Map<String, String> staticParams = actionConfig.getParams();

        if ( staticParams == null || !staticParams.containsKey( key ) )
        {
            return Collections.emptySet();
        }

        final String param = staticParams.get( key );

        HashSet<String> keys = new HashSet<>();

        StringTokenizer t = new StringTokenizer( param, "\t\n\r ," );

        while ( t.hasMoreTokens() )
        {
            keys.add( t.nextToken() );
        }

        return keys;
    }

    public static Collection<ConfigAttribute> getConfigAttributes( ActionConfig actionConfig, String key )
    {
        return getConfigAttributes( getAuthorities( actionConfig, key ) );
    }

    public static Collection<ConfigAttribute> getConfigAttributes( Collection<String> authorities )
    {
        Collection<ConfigAttribute> configAttributes = new HashSet<>();

        for ( String authority : authorities )
        {
            configAttributes.add( new SecurityConfig( authority ) );
        }

        return configAttributes;
    }
}
