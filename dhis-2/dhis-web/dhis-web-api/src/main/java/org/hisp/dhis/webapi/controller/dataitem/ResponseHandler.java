package org.hisp.dhis.webapi.controller.dataitem;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.join;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.hisp.dhis.common.DxfNamespaces.DXF_2_0;
import static org.hisp.dhis.commons.util.SystemUtils.isTestRun;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.MAX_LIMIT;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.USER_UID;
import static org.hisp.dhis.node.NodeUtils.createPager;
import static org.hisp.dhis.webapi.controller.dataitem.DataItemQueryController.API_RESOURCE_PATH;
import static org.hisp.dhis.webapi.controller.dataitem.helper.FilteringHelper.setFiltering;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.hisp.dhis.cache.Cache;
import org.hisp.dhis.cache.CacheProvider;
import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dataitem.query.QueryExecutor;
import org.hisp.dhis.fieldfilter.FieldFilterParams;
import org.hisp.dhis.fieldfilter.FieldFilterService;
import org.hisp.dhis.node.types.CollectionNode;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.user.User;
import org.hisp.dhis.webapi.service.LinkService;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * This class is responsible for handling the result and pagination nodes. This
 * component is coupled to the controller class, where it's being used.
 * 
 * It also keeps an internal cache which's used to speed up the pagination
 * process.
 *
 * IMPORTANT: This cache should be removed once we have a new centralized
 * caching solution in place. At that stage, the new solution should be
 * favoured.
 *
 * @author maikel arabori
 */
@RequiredArgsConstructor
@Component
class ResponseHandler
{
    private static final String CACHE_DATA_ITEMS_PAGINATION = "dataItemsPagination";

    private final QueryExecutor queryExecutor;

    private final LinkService linkService;

    private final FieldFilterService fieldFilterService;

    private final Environment environment;

    private final CacheProvider cacheProvider;

    private Cache<Integer> PAGE_COUNTING_CACHE;

    /**
     * Appends the given dimensionalItemsFound (the collection of results) and
     * fields to the rootNode.
     *
     * @param rootNode the main response root node
     * @param dimensionalItemsFound the collection of results
     * @param fields the list of fields to be returned
     */
    void addResultsToNode( final RootNode rootNode,
        final List<DataItem> dimensionalItemsFound, final Set<String> fields )
    {
        final CollectionNode collectionNode = fieldFilterService.toConcreteClassCollectionNode( DataItem.class,
            new FieldFilterParams( dimensionalItemsFound, newArrayList( fields ) ), "dataItems", DXF_2_0 );

        rootNode.addChild( collectionNode );
    }

    /**
     * This method takes care of the pagination link and their respective
     * attributes. It will count the number of results available and base on the
     * WebOptions will calculate the pagination output.
     *
     * @param rootNode the node where the the pagination will be attached to
     * @param targetEntities the list of classes which requires pagination
     * @param currentUser the current logged user
     * @param options holds the pagination definitions
     * @param filters the query filters used in the count query
     */
    void addPaginationToNode( final RootNode rootNode,
        final List<Class<? extends BaseIdentifiableObject>> targetEntities, final User currentUser,
        final WebOptions options, final Set<String> filters )
    {
        if ( options.hasPaging() && isNotEmpty( targetEntities ) )
        {
            // Defining query params map and setting common params.
            final MapSqlParameterSource paramsMap = new MapSqlParameterSource().addValue( USER_UID,
                currentUser.getUid() );

            setFiltering( filters, options, paramsMap, currentUser );

            final AtomicLong count = new AtomicLong();

            // Counting and summing up the results for each entity.
            targetEntities.parallelStream().forEach( ( entity ) -> {
                count.addAndGet( PAGE_COUNTING_CACHE.get(
                    createPageCountingCacheKey( currentUser, entity, filters, options ),
                    p -> countEntityRowsTotal( entity, options, paramsMap ) ).orElse( 0 ) );
            } );

            final Pager pager = new Pager( options.getPage(), count.get(), options.getPageSize() );

            linkService.generatePagerLinks( pager, API_RESOURCE_PATH );

            rootNode.addChild( createPager( pager ) );
        }
    }

    private int countEntityRowsTotal( final Class<? extends BaseIdentifiableObject> entity, final WebOptions options,
        final MapSqlParameterSource paramsMap )
    {
        // Calculate pagination.
        if ( options.hasPaging() )
        {
            final int maxLimit = options.getPage() * options.getPageSize();
            paramsMap.addValue( MAX_LIMIT, maxLimit );
        }

        return queryExecutor.count( entity, paramsMap );
    }

    private String createPageCountingCacheKey( final User currentUser,
        final Class<? extends BaseIdentifiableObject> entity, final Set<String> filters, final WebOptions options )
    {
        return currentUser.getUsername() + "." + entity + "." + join( "|", filters ) + "."
            + options.getRootJunction().name();
    }

    @PostConstruct
    void init()
    {
        // formatter:off
        PAGE_COUNTING_CACHE = cacheProvider.newCacheBuilder( Integer.class )
            .forRegion( CACHE_DATA_ITEMS_PAGINATION )
            .expireAfterWrite( 5, MINUTES )
            .withInitialCapacity( 1000 )
            .forceInMemory()
            .withMaximumSize( isTestRun( environment.getActiveProfiles() ) ? 0 : 20000 )
            .build();
        // formatter:on
    }
}
