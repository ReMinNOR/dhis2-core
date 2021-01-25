package org.hisp.dhis.security.vote;



import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hisp.dhis.common.CodeGenerator;
import org.hisp.dhis.common.IdentifiableObject;
import org.hisp.dhis.common.IdentifiableObjectManager;
import org.hisp.dhis.document.Document;
import org.hisp.dhis.eventchart.EventChart;
import org.hisp.dhis.eventreport.EventReport;
import org.hisp.dhis.report.Report;
import org.hisp.dhis.sqlview.SqlView;
import org.hisp.dhis.visualization.Visualization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.slf4j.Slf4j;

/**
 * Allows certain type/uid combinations to be externally accessed (no login required).
 *
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
@Slf4j
@Component
public class ExternalAccessVoter implements AccessDecisionVoter<FilterInvocation>
{
    // this should probably be moved somewhere else, but leaving it here for now
    private static final Map<String, Class<? extends IdentifiableObject>> externalClasses = new HashMap<>();

    static
    {
        // TODO charts/reportTables APIs are deprecated and will be removed, clean this up when they are
        externalClasses.put( "charts", Visualization.class );
        externalClasses.put( "reportTables", Visualization.class );
        externalClasses.put( "maps", org.hisp.dhis.mapping.Map.class );
        externalClasses.put( "reports", Report.class );
        externalClasses.put( "documents", Document.class );
        externalClasses.put( "sqlViews", SqlView.class );
        externalClasses.put( "eventReports", EventReport.class );
        externalClasses.put( "eventCharts", EventChart.class );
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private IdentifiableObjectManager manager;

    // -------------------------------------------------------------------------
    // AccessDecisionVoter Implementation
    // -------------------------------------------------------------------------

    @Override
    public boolean supports( ConfigAttribute attribute )
    {
        return false;
    }

    @Override
    public boolean supports( Class<?> clazz )
    {
        return clazz.isAssignableFrom( FilterInvocation.class );
    }

    @Override
    public int vote( Authentication authentication, FilterInvocation filterInvocation,
        Collection<ConfigAttribute> attributes )
    {
        if ( authentication.getPrincipal().equals( "anonymousUser" ) && authentication.isAuthenticated() &&
            filterInvocation.getRequest().getMethod().equals( RequestMethod.GET.name() ) )
        {
            String requestUrl = filterInvocation.getRequestUrl();
            String[] urlSplit = requestUrl.split( "/" );

            if ( urlSplit.length > 3 )
            {
                String type = urlSplit[2];

                if ( urlSplit[1].equals( "api" ) && externalClasses.get( type ) != null )
                {
                    String uid = getUidPart( urlSplit[3] );

                    if ( CodeGenerator.isValidUid( uid ) )
                    {
                        IdentifiableObject identifiableObject = manager.get( externalClasses.get( type ), uid );

                        if ( identifiableObject != null && identifiableObject.getExternalAccess() )
                        {
                            log.debug( "ACCESS_GRANTED [" + filterInvocation.toString() + "]" );

                            return ACCESS_GRANTED;
                        }
                    }
                }
            }
        }

        log.debug( "ACCESS_ABSTAIN [" + filterInvocation.toString() + "]: No supported attributes." );

        return ACCESS_ABSTAIN;
    }

    private String getUidPart( String uidPath )
    {
        if ( uidPath.contains( "." ) )
        {
            return uidPath.substring( 0, uidPath.indexOf( "." ) );
        }

        return uidPath;
    }
}
