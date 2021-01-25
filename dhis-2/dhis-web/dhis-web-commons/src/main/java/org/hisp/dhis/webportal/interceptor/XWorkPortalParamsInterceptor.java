package org.hisp.dhis.webportal.interceptor;



import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.config.entities.ActionConfig;
import com.opensymphony.xwork2.interceptor.Interceptor;

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.system.SystemService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Torgeir Lorange Ostby
 */
public class XWorkPortalParamsInterceptor
    implements Interceptor
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 4915716647953480053L;

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SystemService systemService;

    @Autowired
    public void setSystemService( SystemService systemService )
    {
        this.systemService = systemService;
    }

    // -------------------------------------------------------------------------
    // External configuration
    // -------------------------------------------------------------------------

    private Set<String> standardParams = new HashSet<>();

    public void setStandardParams( Set<String> standardParams )
    {
        this.standardParams = standardParams;
    }

    private Set<String> commaSeparatedParams = new HashSet<>();

    public void setCommaSeparatedParams( Set<String> commaSeparatedParams )
    {
        this.commaSeparatedParams = commaSeparatedParams;
    }

    // -------------------------------------------------------------------------
    // AroundInterceptor implementation
    // -------------------------------------------------------------------------

    @Override
    public void destroy()
    {
    }

    @Override
    public void init()
    {
    }

    @Override
    public String intercept( ActionInvocation actionInvocation )
        throws Exception
    {
        ActionConfig actionConfig = actionInvocation.getProxy().getConfig();

        final Map<String, String> staticParams = actionConfig.getParams();

        if ( staticParams != null )
        {
            // ---------------------------------------------------------------------
            // Push the specified static parameters onto the value stack
            // ---------------------------------------------------------------------

            Map<String, Object> matches = new HashMap<>();

            for ( Map.Entry<String, String> entry : staticParams.entrySet() )
            {
                if ( standardParams.contains( entry.getKey() ) )
                {
                    matches.put( entry.getKey(), entry.getValue() );
                }
                else if ( commaSeparatedParams.contains( entry.getKey() ) )
                {
                    String[] values = entry.getValue().split( "," );

                    for ( int i = 0; i < values.length; i++ )
                    {
                        values[i] = values[i].trim();
                    }

                    matches.put( entry.getKey(), values );
                }
            }

            actionInvocation.getStack().push( matches );
        }

        // TODO: move this to its own systemInfoInterceptor?
        Map<String, Object> systemInfo = new HashMap<>();

        String revision = systemService.getSystemInfo().getRevision();

        if ( StringUtils.isEmpty( revision ) )
        {
            revision = "__dev__";
        }

        systemInfo.put( "buildRevision", revision );
        actionInvocation.getStack().push( systemInfo );

        return actionInvocation.invoke();
    }
}
