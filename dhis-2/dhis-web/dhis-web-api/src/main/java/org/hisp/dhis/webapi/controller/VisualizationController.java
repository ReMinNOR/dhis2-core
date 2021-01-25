package org.hisp.dhis.webapi.controller;



import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.hisp.dhis.common.DimensionalObjectUtils.getDimensions;
import static org.hisp.dhis.schema.descriptors.VisualizationSchemaDescriptor.API_ENDPOINT;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hisp.dhis.common.DimensionService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.i18n.I18nManager;
import org.hisp.dhis.legend.LegendSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.visualization.Visualization;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping( value = API_ENDPOINT )
public class VisualizationController
    extends
    AbstractCrudController<Visualization>
{
    private final LegendSetService legendSetService;

    private final DimensionService dimensionService;

    private final I18nManager i18nManager;

    public VisualizationController( LegendSetService legendSetService, final DimensionService dimensionService,
        final I18nManager i18nManager )
    {
        this.legendSetService = legendSetService;
        this.dimensionService = dimensionService;
        this.i18nManager = i18nManager;
    }

    @Override
    protected Visualization deserializeJsonEntity( final HttpServletRequest request,
        final HttpServletResponse response )
        throws IOException
    {
        final Visualization visualization = super.deserializeJsonEntity( request, response );

        addDimensionsInto( visualization );

        return visualization;
    }

    private void addDimensionsInto( final Visualization visualization )
    {
        if ( visualization != null )
        {
            dimensionService.mergeAnalyticalObject( visualization );

            visualization.getColumnDimensions().clear();
            visualization.getRowDimensions().clear();
            visualization.getFilterDimensions().clear();

            visualization.getColumnDimensions().addAll( getDimensions( visualization.getColumns() ) );
            visualization.getRowDimensions().addAll( getDimensions( visualization.getRows() ) );
            visualization.getFilterDimensions().addAll( getDimensions( visualization.getFilters() ) );

            if ( visualization.getLegendSet() != null )
            {
                visualization.setLegendSet( legendSetService.getLegendSet( visualization.getLegendSet().getUid() ) );
            }
        }
    }

    @Override
    public void postProcessResponseEntity( final Visualization visualization, final WebOptions options,
        final Map<String, String> parameters )
    {
        if ( visualization != null )
        {
            visualization.populateAnalyticalProperties();

            final Set<OrganisationUnit> organisationUnits = currentUserService.getCurrentUser()
                .getDataViewOrganisationUnitsWithFallback();

            for ( OrganisationUnit organisationUnit : visualization.getOrganisationUnits() )
            {
                visualization.getParentGraphMap().put( organisationUnit.getUid(),
                    organisationUnit.getParentGraph( organisationUnits ) );
            }

            final I18nFormat i18nFormat = i18nManager.getI18nFormat();

            if ( isNotEmpty( visualization.getPeriods() ) )
            {
                for ( Period period : visualization.getPeriods() )
                {
                    period.setName( i18nFormat.formatPeriod( period ) );
                }
            }
        }
    }
}
