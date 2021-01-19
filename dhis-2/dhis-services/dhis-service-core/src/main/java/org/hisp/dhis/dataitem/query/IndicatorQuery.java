package org.hisp.dhis.dataitem.query;

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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.hisp.dhis.common.DimensionItemType.INDICATOR;
import static org.hisp.dhis.common.ValueType.NUMBER;
import static org.hisp.dhis.dataitem.query.shared.CommonStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.commonFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.skipNumberValueType;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.commonOrdering;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;

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

        // Very specific case, for Indicator objects, needed to handle filter by value
        // type NUMBER.
        // When the value type filter does not have a NUMBER type, we should not execute
        // this query.
        // It returns an empty instead.
        if ( skipNumberValueType( paramsMap ) )
        {
            return dataItems;
        }

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet( getIndicatorQuery( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();

            viewItem.setName( rowSet.getString( "name" ) );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setDimensionItemType( INDICATOR );

            // Specific case where we have to force a vale type. Indicators don't have a
            // value type but they always evaluate to numbers.
            viewItem.setValueType( NUMBER );
            viewItem.setSimplifiedValueType( NUMBER );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        // Very specific case, for Indicator objects, needed to handle filter by value
        // type NUMBER.
        // When the value type filter does not have a NUMBER type, we should not execute
        // this query.
        // It returns ZERO instead.
        if ( skipNumberValueType( paramsMap ) )
        {
            return 0;
        }

        final StringBuilder sql = new StringBuilder(
            "SELECT COUNT(DISTINCT i.uid)"
                + " FROM indicator i"
                + " WHERE ("
                + sharingConditions( "i", paramsMap )
                + ")" );

        sql.append( commonFiltering( "i", paramsMap ) );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return Indicator.class;
    }

    private String getIndicatorQuery( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT i.\"name\" AS name, i.uid AS uid"
                + " FROM indicator i"
                + " WHERE ("
                + sharingConditions( "i", paramsMap )
                + ")" );

        sql.append( commonFiltering( "i", paramsMap ) );

        sql.append( commonOrdering( "i", paramsMap ) );

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
