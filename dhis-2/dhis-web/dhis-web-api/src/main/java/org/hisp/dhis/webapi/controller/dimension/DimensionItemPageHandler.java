package org.hisp.dhis.webapi.controller.dimension;



import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.hisp.dhis.node.NodeUtils.createPager;
import static org.hisp.dhis.webapi.controller.dimension.DimensionController.RESOURCE_PATH;

import java.util.Map;

import org.hisp.dhis.common.Pager;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.node.types.RootNode;
import org.hisp.dhis.webapi.service.LinkService;
import org.hisp.dhis.webapi.webdomain.WebOptions;
import org.springframework.stereotype.Component;

/**
 * Small component specialized on pagination rules specific to dimension items.
 * This can be seen as a helper extension of
 * {@link DimensionController#getItems(String, Map, OrderParams)}.
 * 
 * @author maikel arabori
 */
@Component
public class DimensionItemPageHandler
{

    private LinkService linkService;

    DimensionItemPageHandler( final LinkService linkService )
    {
        checkNotNull( linkService );
        this.linkService = linkService;
    }

    /**
     * This method will add the pagination child node to the given root node. For
     * this to happen the pagination flag must be set to true. See
     * {@link WebOptions#hasPaging(boolean)}.
     * 
     * @param rootNode the root node where the pagination node will be appended to.
     * @param webOptions the WebOptions settings.
     * @param dimensionUid the uid of the dimension queried in the API url. See
     *        {@link DimensionController#getItems(String, Map, OrderParams)}.
     * @param totalOfItems the total of items. This is represented as page total.
     *        See {@link Pager#getTotal()}.
     */
    void addPaginationToNodeIfEnabled( final RootNode rootNode, final WebOptions webOptions,
        final String dimensionUid, final int totalOfItems )
    {
        final boolean isPaginationEnabled = webOptions.hasPaging( false );

        if ( isPaginationEnabled )
        {
            final String apiRelativeUrl = format( RESOURCE_PATH + "/%s/items", dimensionUid );
            final Pager pager = new Pager( webOptions.getPage(), totalOfItems, webOptions.getPageSize() );

            linkService.generatePagerLinks( pager, apiRelativeUrl );
            rootNode.addChild( createPager( pager ) );
        }
    }
}
