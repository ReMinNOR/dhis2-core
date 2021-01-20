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

import static java.lang.String.join;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.contains;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.substringBetween;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;
import static org.apache.commons.lang3.StringUtils.wrap;
import static org.hisp.dhis.common.ValueType.fromString;
import static org.hisp.dhis.common.ValueType.getAggregatables;
import static org.hisp.dhis.dataitem.query.DataItemQuery.ILIKE_NAME;
import static org.hisp.dhis.dataitem.query.DataItemQuery.USER_GROUP_UIDS;
import static org.hisp.dhis.dataitem.query.DataItemQuery.VALUE_TYPES;
import static org.hisp.dhis.feedback.ErrorCode.E2014;
import static org.hisp.dhis.feedback.ErrorCode.E2016;
import static org.hisp.dhis.webapi.controller.dataitem.DataItemServiceFacade.DATA_TYPE_ENTITY_MAP;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.IllegalQueryException;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.feedback.ErrorMessage;
import org.hisp.dhis.user.User;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

/**
 * Helper class responsible for reading and extracting the URL filters.
 *
 * @author maikel arabori
 */
public class FilteringHelper
{
    private static final String DIMENSION_TYPE_IN_FILTER_PREFIX = "dimensionItemType:in:";

    private static final String DIMENSION_TYPE_EQUAL_FILTER_PREFIX = "dimensionItemType:eq:";

    private static final String VALUE_TYPE_IN_FILTER_PREFIX = "valueType:in:";

    private static final String VALUE_TYPE_EQUAL_FILTER_PREFIX = "valueType:eq:";

    private static final String ILIKE_NAME_FILTER_PREFIX = "name:ilike:";

    private FilteringHelper()
    {
    }

    /**
     * This method will return the respective BaseDimensionalItemObject class
     * from the filter provided.
     *
     * @param filter should have the format of
     *        "dimensionItemType:in:[INDICATOR,DATA_SET,...]", where INDICATOR
     *        and DATA_SET represents the BaseDimensionalItemObject. The valid
     *        types are found at
     *        {@link org.hisp.dhis.common.DataDimensionItemType}
     * @return the respective classes associated with the given IN filter
     * @throws IllegalQueryException if the filter points to a non supported
     *         class/entity.
     */
    public static Set<Class<? extends BaseDimensionalItemObject>> extractEntitiesFromInFilter( final String filter )
    {
        final Set<Class<? extends BaseDimensionalItemObject>> dimensionTypes = new HashSet<>();

        if ( contains( filter, DIMENSION_TYPE_IN_FILTER_PREFIX ) )
        {
            final String[] dimensionTypesInFilter = split( deleteWhitespace( substringBetween( filter, "[", "]" ) ),
                "," );

            if ( isNotEmpty( dimensionTypesInFilter ) )
            {
                for ( final String dimensionType : dimensionTypesInFilter )
                {
                    dimensionTypes.add( entityClassFromString( dimensionType ) );
                }
            }
            else
            {
                throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
            }
        }

        return dimensionTypes;
    }

    /**
     * This method will return the respective BaseDimensionalItemObject class
     * from the filter provided.
     *
     * @param filter should have the format of "dimensionItemType:eq:INDICATOR",
     *        where INDICATOR represents the BaseDimensionalItemObject. It could
     *        be any value represented by
     *        {@link org.hisp.dhis.common.DataDimensionItemType}
     * @return the respective class associated with the given filter
     * @throws IllegalQueryException if the filter points to a non supported
     *         class/entity.
     */
    public static Class<? extends BaseDimensionalItemObject> extractEntityFromEqualFilter( final String filter )
    {
        final byte DIMENSION_TYPE = 2;
        Class<? extends BaseDimensionalItemObject> entity = null;

        if ( hasEqualsDimensionTypeFilter( filter ) )
        {
            final String[] array = filter.split( ":" );
            final boolean hasDimensionType = array.length == 3;

            if ( hasDimensionType )
            {
                entity = entityClassFromString( array[DIMENSION_TYPE] );
            }
            else
            {
                throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
            }
        }

        return entity;
    }

    /**
     * This method will return the respective ValueType from the filter
     * provided.
     *
     * @param filter should have the format of
     *        "valueType:in:[TEXT,BOOLEAN,NUMBER,...]", where TEXT and BOOLEAN
     *        represents the ValueType. The valid types are found at
     *        {@link ValueType}
     * @return the respective classes associated with the given IN filter
     * @throws IllegalQueryException if the filter points to a non supported
     *         value type.
     */
    public static Set<String> extractValueTypesFromInFilter( final String filter )
    {
        final Set<String> valueTypes = new HashSet<>();

        if ( contains( filter, VALUE_TYPE_IN_FILTER_PREFIX ) )
        {
            final String[] valueTypesInFilter = split( deleteWhitespace( substringBetween( filter, "[", "]" ) ),
                "," );

            if ( isNotEmpty( valueTypesInFilter ) )
            {
                for ( final String valueType : valueTypesInFilter )
                {
                    valueTypes.add( getValueTypeOrThrow( valueType ) );
                }
            }
            else
            {
                throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
            }
        }

        return valueTypes;
    }

    /**
     * This method will return the respective ValueType from the filter
     * provided.
     *
     * @param filter should have the format of "valueType:eq:NUMBER", where
     *        NUMBER represents the ValueType. It could be any value represented
     *        by {@link ValueType}
     * @return the respective value type associated with the given filter
     * @throws IllegalQueryException if the filter points to a non supported
     *         value type.
     */
    public static String extractValueTypeFromEqualFilter( final String filter )
    {
        final byte VALUE_TYPE = 2;
        String valueType = null;

        if ( hasEqualsValueTypeFilter( filter ) )
        {
            final String[] array = filter.split( ":" );
            final boolean hasValueType = array.length == 3;

            if ( hasValueType )
            {
                valueType = getValueTypeOrThrow( array[VALUE_TYPE] );
            }
            else
            {
                throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
            }
        }

        return valueType;
    }

    /**
     * This method will return ALL respective ValueType's from the filter. It
     * will merge both EQ and IN conditions into a single Set object.
     *
     * @param filters coming from the URL params/filters
     * @return all respective value type's associated with the given filter
     * @throws IllegalQueryException if the filter points to a non supported
     *         value type.
     */
    public static Set<String> extractAllValueTypesFromFilters( final List<String> filters )
    {
        final Set<String> valueTypes = new HashSet<>();

        final Iterator<String> iterator = filters.iterator();

        while ( iterator.hasNext() )
        {
            final String filter = iterator.next();
            final Set<String> multipleValueTypes = extractValueTypesFromInFilter( filter );
            final String singleValueType = extractValueTypeFromEqualFilter( filter );

            if ( CollectionUtils.isNotEmpty( multipleValueTypes ) )
            {
                valueTypes.addAll( multipleValueTypes );
            }

            if ( singleValueType != null )
            {
                valueTypes.add( singleValueType );
            }
        }

        return valueTypes;
    }

    public static String extractValueFromIlikeNameFilter( final List<String> filters )
    {
        final byte ILIKE_VALUE = 2;

        if ( CollectionUtils.isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                if ( hasIlikeNameFilter( filter ) )
                {
                    final String[] array = filter.split( ":" );
                    final boolean hasIlikeValue = array.length == 3;

                    if ( hasIlikeValue )
                    {
                        return trimToEmpty( array[ILIKE_VALUE] );
                    }
                    else
                    {
                        throw new IllegalQueryException( new ErrorMessage( E2014, filter ) );
                    }
                }
            }
        }

        return EMPTY;
    }

    /**
     * Sets the filtering defined by filters list into the paramsMap.
     *
     * @param filters the source of filtering params
     * @param paramsMap the map that will receive the filtering params
     * @param currentUser the current user logged
     */
    public static void setFiltering( final List<String> filters, final MapSqlParameterSource paramsMap,
        final User currentUser )
    {
        final String ilikeName = extractValueFromIlikeNameFilter( filters );

        if ( isNotBlank( ilikeName ) )
        {
            paramsMap.addValue( ILIKE_NAME, wrap( ilikeName, "%" ) );
        }

        if ( containsValueTypeFilter( filters ) )
        {
            final Set<String> valueTypesFilter = extractAllValueTypesFromFilters( filters );
            assertThatValueTypeFilterHasOnlyAggregatableTypes( valueTypesFilter, filters );

            paramsMap.addValue( VALUE_TYPES, extractAllValueTypesFromFilters( filters ) );
        }
        else
        {
            // Includes all value types.
            paramsMap.addValue( VALUE_TYPES,
                getAggregatables().stream().map( type -> type.name() ).collect( toSet() ) );
        }

        // Add user group filtering, when present.
        if ( currentUser != null && CollectionUtils.isNotEmpty( currentUser.getGroups() ) )
        {
            final Set<String> userGroupUids = currentUser.getGroups().stream()
                .filter( group -> group != null )
                .map( group -> group.getUid() )
                .collect( toSet() );
            paramsMap.addValue( USER_GROUP_UIDS, "{" + join( ",", userGroupUids ) + "}" );
        }
    }

    private static String getValueTypeOrThrow( final String valueType )
    {
        try
        {
            return fromString( valueType ).name();
        }
        catch ( IllegalArgumentException e )
        {
            throw new IllegalQueryException(
                new ErrorMessage( E2016, valueType, "valueType", ValueType.getAggregatables() ) );
        }
    }

    /**
     * Simply checks if the given list of filters contains a dimension type
     * filter.
     *
     * @param filters
     * @return true if a dimension type filter is found, false otherwise.
     */
    public static boolean containsDimensionTypeFilter( final List<String> filters )
    {
        if ( CollectionUtils.isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                if ( hasEqualsDimensionTypeFilter( filter ) || hasInDimensionTypeFilter( filter ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Simply checks if the given list of filters contains a value type filter.
     *
     * @param filters
     * @return true if a value type filter is found, false otherwise.
     */
    public static boolean containsValueTypeFilter( final List<String> filters )
    {
        if ( CollectionUtils.isNotEmpty( filters ) )
        {
            for ( final String filter : filters )
            {
                if ( hasEqualsValueTypeFilter( filter ) || hasInValueTypeFilter( filter ) )
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Simply checks if the given set of ValueType names contains a valid value
     * type filter. Only aggregatable types are considered valid for this case.
     *
     * @param valueTypeNames
     * @throws IllegalQueryException if the given Set<String> contains
     *         non-aggregatable value types.
     */
    public static void assertThatValueTypeFilterHasOnlyAggregatableTypes( final Set<String> valueTypeNames,
        final List<String> filters )
    {
        if ( CollectionUtils.isNotEmpty( valueTypeNames ) )
        {
            final List<String> aggregatableTypes = getAggregatables().stream().map( v -> v.name() ).collect( toList() );

            for ( final String valueType : valueTypeNames )
            {
                if ( !aggregatableTypes.contains( valueType ) )
                {
                    throw new IllegalQueryException(
                        new ErrorMessage( E2016, valueType, filters, ValueType.getAggregatables() ) );
                }
            }
        }
    }

    public static boolean hasEqualsValueTypeFilter( final String filter )
    {
        return trimToEmpty( filter ).contains( VALUE_TYPE_EQUAL_FILTER_PREFIX );
    }

    public static boolean hasInValueTypeFilter( final String filter )
    {
        return trimToEmpty( filter ).contains( VALUE_TYPE_IN_FILTER_PREFIX );
    }

    public static boolean hasIlikeNameFilter( final String filter )
    {
        return trimToEmpty( filter ).contains( ILIKE_NAME_FILTER_PREFIX );
    }

    private static boolean hasEqualsDimensionTypeFilter( final String filter )
    {
        return trimToEmpty( filter ).contains( DIMENSION_TYPE_EQUAL_FILTER_PREFIX );
    }

    private static boolean hasInDimensionTypeFilter( final String filter )
    {
        return trimToEmpty( filter ).contains( DIMENSION_TYPE_IN_FILTER_PREFIX );
    }

    private static Class<? extends BaseDimensionalItemObject> entityClassFromString( final String entityType )
    {
        final Class<? extends BaseDimensionalItemObject> entity = DATA_TYPE_ENTITY_MAP.get( entityType );

        if ( entity == null )
        {
            throw new IllegalQueryException(
                new ErrorMessage( E2016, entityType, "dimensionItemType", DATA_TYPE_ENTITY_MAP.keySet() ) );
        }

        return entity;
    }
}
