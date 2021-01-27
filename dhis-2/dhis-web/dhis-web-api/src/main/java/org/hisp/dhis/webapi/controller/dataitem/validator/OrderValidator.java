package org.hisp.dhis.webapi.controller.dataitem.validator;

import static java.util.Arrays.asList;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.hisp.dhis.feedback.ErrorCode.E2035;

import java.util.Set;

import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.feedback.ErrorMessage;

/**
 * Validator class responsible for validating order parameters.
 *
 * @author maikel arabori
 */
public class OrderValidator
{
    public static final byte ORDERING_ATTRIBUTE_NAME = 0;

    public static final byte ORDERING_VALUE = 1;

    public static final String DESC = "desc";

    public static final String ASC = "asc";

    private OrderValidator()
    {
    }

    /**
     * Checks if the given set o filters are valid, and contains only filter
     * names and operators supported.
     *
     * @param orderParams a set containing elements in the format
     *        "attributeName:asc"
     * @throws IllegalQueryException if the set contains a non-supported name or
     *         operator, or contains invalid syntax.
     */
    public static void validateOrderParams( final Set<String> orderParams )
    {
        if ( isNotEmpty( orderParams ) )
        {
            for ( final String orderParam : orderParams )
            {
                final String[] orderAttributeValuePair = orderParam.split( ":" );
                final String orderAttributeName = trimToEmpty( orderAttributeValuePair[ORDERING_ATTRIBUTE_NAME] );
                final String orderValue = trimToEmpty( orderAttributeValuePair[ORDERING_VALUE] );

                // Check for valid order attribute name. Only DataItem
                // attributes are allowed.
                if ( asList( DataItem.class.getDeclaredFields() ).stream()
                    .noneMatch( field -> orderAttributeName.equals( field.getName() ) ) )
                {
                    throw new IllegalQueryException( new ErrorMessage( E2035, orderAttributeName ) );
                }

                // Check for valid ordering. Only "asc" and "desc" are allowed.
                if ( !orderValue.equals( DESC ) && !orderValue.equals( ASC ) )
                {
                    throw new IllegalQueryException( new ErrorMessage( E2035, orderValue ) );
                }
            }
        }
    }

    /**
     * Matches all current DataItem attributes with the given orderParam.
     * 
     * @param orderParam the order param to be matched
     * @throws IllegalQueryException if the orderParam does not match any
     *         DataItem attribute
     */
    private static void validateOrderParam( final String orderParam )
    {
        if ( asList( DataItem.class.getDeclaredFields() ).stream()
            .noneMatch( field -> orderParam.equals( field.getName() ) ) )
        {
            throw new IllegalQueryException(
                new ErrorMessage( E2035, substringBeforeLast( orderParam, ":" ) ) );
        }
    }
}
