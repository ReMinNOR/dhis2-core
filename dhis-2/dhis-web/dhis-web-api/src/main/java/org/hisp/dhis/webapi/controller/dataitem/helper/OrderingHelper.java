package org.hisp.dhis.webapi.controller.dataitem.helper;

/*
 * Copyright (c) 2004-2021, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.hisp.dhis.feedback.ErrorCode.E2015;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.collections4.comparators.NullComparator;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dxf2.common.OrderParams;
import org.hisp.dhis.feedback.ErrorMessage;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Helper class responsible for providing sorting capabilities.
 *
 * @author maikel arabori
 */
public class OrderingHelper
{
    private static final int ORDERING_ATTRIBUTE_NAME = 0;

    private static final int ORDERING_ATTRIBUTE_VALUE = 1;

    private static final String DESC = "desc";

    private OrderingHelper()
    {
    }

    /**
     * Sorts the given list based on the given sorting params.
     *
     * @param dimensionalItems
     * @param sortingParams
     */
    public static void sort( final List<DataItem> dimensionalItems, final OrderParams sortingParams )
    {
        if ( sortingParams != null && isNotEmpty( dimensionalItems ) )
        {
            final ComparatorChain<DataItem> chainOfComparators = new ComparatorChain<>();
            final Set<String> orderingPairs = sortingParams.getOrders();

            if ( isNotEmpty( orderingPairs ) )
            {
                for ( final String orderingPair : orderingPairs )
                {
                    chainOfComparators.addComparator( getComparator( orderingPair, dimensionalItems ) );
                }

                dimensionalItems.sort( chainOfComparators );
            }
        }
    }

    /**
     * Sets the ordering defined by orderParams into the paramsMap. It will set
     * the given "orderParams" into the provided "paramsMap". It's important to
     * highlight that the "key" added to the "paramsMap" will contain the actual
     * order param, ie.: "name" + "Order". So, if there is a "name" as order
     * param, the "key" will result in "nameOrder".
     *
     * @param orderParams the source of ordering params
     * @param paramsMap the map that will receive the order params
     */
    public static void setOrdering( final OrderParams orderParams, final MapSqlParameterSource paramsMap )
    {
        if ( orderParams != null && isNotEmpty( orderParams.getOrders() ) )
        {
            final Set<String> orders = orderParams.getOrders();

            for ( final String order : orders )
            {
                final String[] array = order.split( ":" );

                // Concatenation of param name (ie.:"name") + "Order". It will
                // result
                // in "nameOrder".
                paramsMap.addValue( trimToEmpty( array[0] ).concat( "Order" ), array[1] );
            }
        }
    }

    @SuppressWarnings( { "unchecked", "rawtypes" } )
    private static Comparator<DataItem> getComparator( final String orderingParam,
        final List<DataItem> dimensionalItems )
    {
        final String[] orderingAttributes = split( orderingParam, ":" );
        final boolean hasValidOrderingAttributes = orderingAttributes != null && orderingAttributes.length == 2;

        if ( hasValidOrderingAttributes )
        {
            final BeanComparator<DataItem> comparator = new BeanComparator(
                orderingAttributes[ORDERING_ATTRIBUTE_NAME], new NullComparator<>( true ) );

            if ( DESC.equals( orderingAttributes[ORDERING_ATTRIBUTE_VALUE] ) )
            {
                return comparator.reversed();
            }

            return comparator;
        }
        else
        {
            throw new IllegalQueryException( new ErrorMessage( E2015, orderingParam ) );
        }
    }
}
