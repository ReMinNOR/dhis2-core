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
package org.hisp.dhis.dataitem.query;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hisp.dhis.common.DimensionItemType.INDICATOR;
import static org.hisp.dhis.common.ValueType.NUMBER;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.skipValueType;
import static org.hisp.dhis.dataitem.query.shared.LimitStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.displayColumnOrdering;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.nameOrdering;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.indicator.Indicator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * Indicators.
 *
 * @author maikel arabori
 */
@Component
public class IndicatorQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public IndicatorQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    @Override
    public List<DataItem> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItem> dataItems = new ArrayList<>();

        // Very specific case, for Indicator objects, needed to handle filter by
        // value type NUMBER.
        // When the value type filter does not have a NUMBER type, we should not
        // execute this query.
        // It returns an empty instead.
        if ( skipValueType( NUMBER, paramsMap ) )
        {
            return dataItems;
        }

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet( getIndicatorQuery( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();

            viewItem.setName( rowSet.getString( "name" ) );
            viewItem.setDisplayName( defaultIfBlank( rowSet.getString( "i18n_name" ), rowSet.getString( "name" ) ) );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( INDICATOR.name() );

            // Specific case where we have to force a vale type. Indicators
            // don't have a value type but they always evaluate to numbers.
            viewItem.setValueType( NUMBER.name() );
            viewItem.setSimplifiedValueType( NUMBER.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        // Very specific case, for Indicator objects, needed to handle filter by
        // value type NUMBER.
        // When the value type filter does not have a NUMBER type, we should not
        // execute this query.
        // It returns ZERO instead.
        if ( skipValueType( NUMBER, paramsMap ) )
        {
            return 0;
        }

        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getIndicatorQuery( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return Indicator.class;
    }

    private String getIndicatorQuery( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT indicator.uid, indicator.\"name\", indicator.code" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append( ", displayname.value AS i18n_name" );
        }

        sql.append( " FROM indicator " );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append(
                " LEFT JOIN jsonb_to_recordset(indicator.translations) as displayname(value TEXT, locale TEXT, property TEXT) ON displayname.locale = :"
                    + LOCALE + " AND displayname.property = 'NAME'" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "indicator", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "indicator", paramsMap ) );

        if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( DISPLAY_NAME ),
                DISPLAY_NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( DISPLAY_NAME ), DISPLAY_NAME + " cannot be null/blank." );

            if ( paramsMap.hasValue( LOCALE ) )
            {
                isInstanceOf( String.class, paramsMap.getValue( LOCALE ),
                    LOCALE + " cannot be null and must be a String." );
                hasText( (String) paramsMap.getValue( LOCALE ), LOCALE + " cannot be null/blank." );

                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND displayname.property = 'NAME' AND displayname.value ILIKE :" + DISPLAY_NAME )
                    .append( " UNION " )
                    .append(
                        " SELECT indicator.uid, indicator.\"name\", indicator.code, indicator.\"name\" AS i18n_name" )
                    .append(
                        " FROM indicator, jsonb_to_recordset(indicator.translations) AS displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE indicator.uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT indicator.uid FROM indicator," )
                    .append(
                        " jsonb_to_recordset(indicator.translations) AS displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " AND indicator.\"name\" ILIKE :" + DISPLAY_NAME )
                    .append( " UNION " )
                    .append(
                        " SELECT indicator.uid, indicator.\"name\", indicator.code, indicator.\"name\" AS i18n_name" )
                    .append( " FROM indicator" )
                    .append( " WHERE (indicator.translations = '[]' OR indicator.translations IS NULL)" )
                    .append( " AND indicator.\"name\" ILIKE :" + DISPLAY_NAME );

                sql.append( displayNameQuery.toString() );
            }
            else
            {
                // No locale, so we default the comparison to the raw name.
                sql.append( " AND (indicator.\"name\" ILIKE :" + NAME + ")" );
            }
        }

        sql.append( " GROUP BY indicator.uid, indicator.\"name\", indicator.code" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append( ", i18n_name" );
        }

        if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME_ORDER )
            && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
        {
            // 4 means i18n_name
            sql.append( displayColumnOrdering( 4, paramsMap ) );
        }
        else if ( paramsMap != null && paramsMap.hasValue( NAME_ORDER )
            && isNotBlank( (String) paramsMap.getValue( NAME_ORDER ) ) )
        {

            sql.append( nameOrdering( "indicator", paramsMap ) );
        }

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
