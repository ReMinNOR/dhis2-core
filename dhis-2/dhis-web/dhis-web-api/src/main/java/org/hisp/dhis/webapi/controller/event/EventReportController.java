package org.hisp.dhis.webapi.controller.event;



import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.eventreport.EventReport;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.schema.descriptors.EventReportSchemaDescriptor;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.controller.AbstractCrudController;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.hisp.dhis.common.DimensionalObjectUtils.getDimensions;

/**
 * @author Lars Helge Overland
 */
@Controller
@RequestMapping( value = EventReportSchemaDescriptor.API_ENDPOINT )
public class EventReportController
    extends AbstractCrudController<EventReport>
{
    @Autowired
    private DimensionService dimensionService;

    @Autowired
    private I18nManager i18nManager;

    //--------------------------------------------------------------------------
    // CRUD
    //--------------------------------------------------------------------------

    @Override
    protected EventReport deserializeJsonEntity( HttpServletRequest request, HttpServletResponse response ) throws IOException
    {
        EventReport eventReport = super.deserializeJsonEntity( request, response );
        mergeEventReport( eventReport );

        return eventReport;
    }

    //--------------------------------------------------------------------------
    // Hooks
    //--------------------------------------------------------------------------

    @Override
    protected void postProcessResponseEntity( EventReport report, WebOptions options, Map<String, String> parameters )
        throws Exception
    {
        report.populateAnalyticalProperties();

        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser != null )
        {
            Set<OrganisationUnit> roots = currentUser.getDataViewOrganisationUnitsWithFallback();

            for ( OrganisationUnit organisationUnit : report.getOrganisationUnits() )
            {
                report.getParentGraphMap().put( organisationUnit.getUid(), organisationUnit.getParentGraph( roots ) );
            }
        }

        I18nFormat format = i18nManager.getI18nFormat();

        if ( report.getPeriods() != null && !report.getPeriods().isEmpty() )
        {
            for ( Period period : report.getPeriods() )
            {
                period.setName( format.formatPeriod( period ) );
            }
        }
    }

    //--------------------------------------------------------------------------
    // Supportive methods
    //--------------------------------------------------------------------------

    private void mergeEventReport( EventReport report )
    {
        dimensionService.mergeAnalyticalObject( report );
        dimensionService.mergeEventAnalyticalObject( report );

        report.getColumnDimensions().clear();
        report.getRowDimensions().clear();
        report.getFilterDimensions().clear();

        report.getColumnDimensions().addAll( getDimensions( report.getColumns() ) );
        report.getRowDimensions().addAll( getDimensions( report.getRows() ) );
        report.getFilterDimensions().addAll( getDimensions( report.getFilters() ) );
    }
}
