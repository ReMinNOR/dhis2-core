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
 * Validator class responsible for validating the filter parameters.
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
    public static void validateFilters( final Set<String> filters )
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
}
