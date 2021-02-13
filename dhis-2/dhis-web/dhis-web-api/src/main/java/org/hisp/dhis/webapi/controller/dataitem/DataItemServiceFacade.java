package org.hisp.dhis.webapi.controller.dataitem;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.hisp.dhis.webapi.controller.dataitem.Filter.Combination.DIMENSION_TYPE_EQUAL;
import static org.hisp.dhis.webapi.controller.dataitem.Filter.Combination.DIMENSION_TYPE_IN;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntitiesFromInFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.extractEntityFromEqualFilter;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.setFiltering;
import static org.hisp.dhis.webapi.controller.dataitem.helper.OrderingHelper.setOrdering;
import static org.hisp.dhis.webapi.controller.dataitem.helper.OrderingHelper.sort;
import static org.hisp.dhis.webapi.controller.dataitem.helper.PaginationHelper.paginate;
import static org.hisp.dhis.webapi.controller.dataitem.helper.PaginationHelper.setMaxResultsWhenPaging;
import static org.hisp.dhis.webapi.controller.dataitem.validator.FilterValidator.containsFilterWithOneOfPrefixes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dataitem.query.QueryExecutor;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.hisp.dhis.program.ProgramIndicatorGroup;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeDimensionItem;
import org.hisp.dhis.security.acl.AclService;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is tight to the controller layer and is responsible to encapsulate
 * logic that does not belong to the controller but does not belong to the
 * service layer either. In other words, these set of methods sit between the
 * controller and service layers. The main goal is to alleviate the controller
 * layer.
 *
 * @author maikel arabori
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataItemServiceFacade
{
    private final CurrentUserService currentUserService;

    private final AclService aclService;

    private final QueryExecutor queryExecutor;

    /**
     * This Map holds the allowed data types to be queried.
     */
    // @formatter:off
    public static final Map<String, Class<? extends BaseIdentifiableObject>> DATA_TYPE_ENTITY_MAP = ImmutableMap
        .<String, Class<? extends BaseIdentifiableObject>> builder()
            .put( "INDICATOR", Indicator.class )
            .put( "DATA_ELEMENT", DataElement.class )
            .put( "DATA_SET", DataSet.class )
            .put( "PROGRAM_INDICATOR", ProgramIndicator.class )
            .put( "PROGRAM_DATA_ELEMENT", ProgramDataElementDimensionItem.class )
            .put( "PROGRAM_ATTRIBUTE", ProgramTrackedEntityAttributeDimensionItem.class )
            .put( "PROGRAM_INDICATOR_GROUP", ProgramIndicatorGroup.class)
            .build();
    // @formatter:on

    /**
     * This method will iterate through the list of target entities, and query
     * each one of them using the filters and params provided. The result list
     * will bring together the results of all target entities queried.
     * 
     * @param targetEntities the list of entities to be retrieved
     * @param orderParams request ordering params
     * @param filters request filters
     * @param options request options
     * @return the consolidated collection of entities found.
     */
    List<DataItem> retrieveDataItemEntities(
        final Set<Class<? extends BaseIdentifiableObject>> targetEntities, final Set<String> filters,
        final WebOptions options, final OrderParams orderParams )
    {
        final List<DataItem> dataItems = new ArrayList<>();

        final User currentUser = currentUserService.getCurrentUser();

        if ( isNotEmpty( targetEntities ) )
        {
            // Defining the query params map, and setting the common params.
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( "userUid",
                currentUser.getUid() );

            setFiltering( filters, options, paramsMap, currentUser );

            setOrdering( orderParams, paramsMap );

            setMaxResultsWhenPaging( options, paramsMap );

            // Retrieving all items for each entity type.
            targetEntities.parallelStream().forEach( ( entity ) -> {
                if ( aclService.canRead( currentUser, entity ) )
                {
                    dataItems.addAll( queryExecutor.find( entity, paramsMap ) );
                }
            } );

            // In memory sorting.
            sort( dataItems, orderParams );

            // In memory pagination.
            return paginate( options, dataItems );
        }

        return dataItems;
    }

    /**
     * This method returns a set of BaseDimensionalItemObject's based on the
     * provided filters.
     *
     * @param filters
     * @return the data items classes to be queried
     */
    Set<Class<? extends BaseIdentifiableObject>> extractTargetEntities( final Set<String> filters )
    {
        final Set<Class<? extends BaseIdentifiableObject>> targetedEntities = new HashSet<>( 0 );

        if ( containsFilterWithOneOfPrefixes( filters, DIMENSION_TYPE_EQUAL.getCombination(),
            DIMENSION_TYPE_IN.getCombination() ) )
        {
            addFilteredTargetEntities( filters, targetedEntities );
        }
        else
        {
            // If no filter is set we search for all entities.
            targetedEntities.addAll( DATA_TYPE_ENTITY_MAP.values() );
        }

        return targetedEntities;
    }

    private void addFilteredTargetEntities( final Set<String> filters,
        final Set<Class<? extends BaseIdentifiableObject>> targetedEntities )
    {
        final Iterator<String> iterator = filters.iterator();

        while ( iterator.hasNext() )
        {
            final String filter = iterator.next();
            final Class<? extends BaseIdentifiableObject> entity = extractEntityFromEqualFilter( filter );
            final Set<Class<? extends BaseIdentifiableObject>> entities = extractEntitiesFromInFilter( filter );

            if ( entity != null || isNotEmpty( entities ) )
            {
                if ( entity != null )
                {
                    targetedEntities.add( entity );
                }

                if ( isNotEmpty( entities ) )
                {
                    targetedEntities.addAll( entities );
                }
            }
        }
    }
}
