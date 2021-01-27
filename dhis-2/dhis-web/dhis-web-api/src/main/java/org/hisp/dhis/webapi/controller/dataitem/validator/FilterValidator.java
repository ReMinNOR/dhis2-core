package org.hisp.dhis.webapi.controller.dataitem.validator;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.hisp.dhis.feedback.ErrorCode.E2014;
import static org.hisp.dhis.feedback.ErrorCode.E2032;
import static org.hisp.dhis.feedback.ErrorCode.E2033;
import static org.hisp.dhis.webapi.controller.dataitem.Filter.Attribute.getNames;
import static org.hisp.dhis.webapi.controller.dataitem.Filter.Operation.getAbbreviations;

import java.util.Set;

import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.feedback.ErrorMessage;

/**
 * Validator class responsible for validating filter parameters.
 * 
 * @author maikel arabori
 */
public class FilterValidator
{
    /**
     * Checks if the given set o filters are valid, and contains only filter
     * names and operators supported.
     *
     * @param filters in the format filterName:eq:aWord
     * @throws IllegalQueryException if the set contains a non-supported name or
     *         operator, or and invalid syntax.
     */
    public static void validateNamesAndOperators( final Set<String> filters )
    {
        final byte FILTER_NAME = 0;
        final byte FILTER_OPERATOR = 1;

        if ( isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                {
                    final String[] array = filter.split( ":" );
                    final boolean filterHasCorrectForm = array.length == 3;

                    if ( filterHasCorrectForm )
                    {
                        final String filterName = trimToEmpty( array[FILTER_NAME] );
                        final String operator = trimToEmpty( array[FILTER_OPERATOR] );

                        if ( !getNames().contains( filterName ) )
                        {
                            throw new IllegalQueryException( new ErrorMessage( E2032, filterName ) );
                        }

                        if ( !getAbbreviations().contains( operator ) )
                        {
                            throw new IllegalQueryException( new ErrorMessage( E2033, operator ) );
                        }
                    }
                    else
                    {
                        throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
                    }
                }
            }
        }
    }

    /**
     * Simply checks if the given set of filters contains the given filter
     * prefix.
     *
     * @param filters
     * @param withPrefix
     * @return true if a dimension type filter is found, false otherwise.
     */
    public static boolean containsFilterWithPrefix( final Set<String> filters, final String withPrefix )
    {
        if ( isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                if ( filterHasPrefix( filter, withPrefix ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Simply checks if the given set of filters contains the given filter
     * prefix.
     *
     * @param filters
     * @param withPrefixOne
     * @param withPrefixTwo
     * @return true if a dimension type filter is found, false otherwise.
     */
    public static boolean containsFilterWithOneOfPrefixes( final Set<String> filters, final String withPrefixOne,
        final String withPrefixTwo )
    {
        if ( isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                if ( filterHasOneOfPrefixes( filter, withPrefixOne, withPrefixTwo ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Simply checks if a given filter start the prefix provided.
     * 
     * @param filter the full filter param, in the format: name:eq:someName,
     *        where 'name' is the attribute and 'eq' is the operator
     * @param prefix the prefix to be matched. See
     *        {@link org.hisp.dhis.webapi.controller.dataitem.Filter.Prefix} for
     *        valid ones
     * @return true if the current filter starts with given prefix, false
     *         otherwise
     */
    public static boolean filterHasPrefix( final String filter, final String prefix )
    {
        return trimToEmpty( filter ).startsWith( prefix );
    }

    /**
     * Simply checks if a given filter starts with any one of the prefix
     * provided.
     * 
     * @param filter the full filter param, in the format: name:eq:someName,
     *        where 'name' is the attribute and 'eq' is the operator
     * @param prefixOne the first prefix to be matched. See
     *        {@link org.hisp.dhis.webapi.controller.dataitem.Filter.Prefix} for
     *        valid ones
     * @param prefixTwo the second prefix to be matched. See
     *        {@link org.hisp.dhis.webapi.controller.dataitem.Filter.Prefix} for
     *        valid ones
     * @return true if the current filter starts with any one of the given
     *         prefixes, false otherwise
     */
    public static boolean filterHasOneOfPrefixes( final String filter, final String prefixOne,
        final String prefixTwo )
    {
        return trimToEmpty( filter ).startsWith( prefixOne ) || trimToEmpty( filter ).startsWith( prefixTwo );
    }
}
