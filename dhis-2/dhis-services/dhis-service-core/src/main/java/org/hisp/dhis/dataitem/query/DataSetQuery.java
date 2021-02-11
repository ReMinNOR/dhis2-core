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
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.hisp.dhis.common.DimensionItemType.REPORTING_RATE;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.uidFiltering;
import static org.hisp.dhis.dataitem.query.shared.LimitStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.displayColumnOrdering;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.nameOrdering;
import static org.hisp.dhis.dataitem.query.shared.ParamPresenceChecker.hasStringPresence;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.DISPLAY_NAME;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.DISPLAY_NAME_ORDER;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.LOCALE;
import static org.hisp.dhis.dataitem.query.shared.QueryParam.NAME_ORDER;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.dataset.DataSet;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * DataSets.
 *
 * @author maikel arabori
 */
@Component
public class DataSetQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DataSetQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    @Override
    public List<DataItem> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItem> dataItems = new ArrayList<>();

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
            getDateSetQuery( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();
            final String name = trimToNull( rowSet.getString( "name" ) );
            final String displayName = defaultIfBlank( trimToNull( rowSet.getString( "i18n_name" ) ), name );

            viewItem.setName( name );
            viewItem.setDisplayName( displayName );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( REPORTING_RATE.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getDateSetQuery( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return DataSet.class;
    }

    private String getDateSetQuery( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append(
            "SELECT dataset.uid, dataset.\"name\", dataset.code" );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append( ", displayname.value AS i18n_name" );
        }
        else
        {
            sql.append( ", dataset.\"name\" AS i18n_name" );
        }

        sql.append( " FROM dataset " );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append(
                ", jsonb_to_recordset(dataset.translations) as displayname(value TEXT, locale TEXT, property TEXT)" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "dataset", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "dataset", paramsMap ) );

        sql.append( uidFiltering( "dataset", paramsMap ) );

        sql.append( specificDisplayNameFilter( paramsMap ) );

        sql.append( specificLocaleFilter( paramsMap ) );

        sql.append( " GROUP BY dataset.uid, dataset.\"name\", dataset.code" );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append( ", i18n_name" );
        }

        if ( hasStringPresence( paramsMap, DISPLAY_NAME_ORDER ) )
        {
            if ( hasStringPresence( paramsMap, DISPLAY_NAME ) )
            {
                // 4 means i18n_name
                sql.append( displayColumnOrdering( 4, paramsMap ) );
            }
            else
            {
                // 2 means name
                sql.append( displayColumnOrdering( 2, paramsMap ) );
            }
        }
        else if ( hasStringPresence( paramsMap, NAME_ORDER ) )
        {
            sql.append( nameOrdering( "dataset", paramsMap ) );
        }

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }

    private String specificDisplayNameFilter( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        if ( hasStringPresence( paramsMap, DISPLAY_NAME ) )
        {
            if ( hasStringPresence( paramsMap, LOCALE ) )
            {
                sql.append( fetchDisplayName( paramsMap, true ) );
            }
            else
            {
                // User does not have any locale set.
                sql.append( " AND ( dataset.\"name\" ILIKE :" + DISPLAY_NAME + ")" );
            }
        }

        return sql.toString();
    }

    private String specificLocaleFilter( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        if ( !hasStringPresence( paramsMap, DISPLAY_NAME ) && hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append( fetchDisplayName( paramsMap, false ) );
        }

        return sql.toString();
    }

    private String fetchDisplayName( final MapSqlParameterSource paramsMap, boolean filterByDisplayName )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( " AND displayname.locale = :" + LOCALE )
            .append( " AND displayname.property = 'NAME'" );

        if ( filterByDisplayName )
        {
            sql.append( " AND displayname.value ILIKE :" + DISPLAY_NAME );
        }

        sql.append( " UNION " )
            .append(
                " SELECT dataset.uid, dataset.\"name\", dataset.code, dataset.\"name\" AS i18n_name" )
            .append(
                " FROM dataset, jsonb_to_recordset(dataset.translations) AS displayname(locale TEXT, property TEXT)" )
            .append( " WHERE dataset.uid" )
            .append( " NOT IN (" )
            .append( " SELECT dataset.uid FROM dataset," )
            .append(
                " jsonb_to_recordset(dataset.translations) AS displayname(locale TEXT, property TEXT)" )
            .append( " WHERE displayname.locale = :" + LOCALE )
            .append( ")" )
            .append( " AND displayname.property = 'NAME'" );

        if ( filterByDisplayName )
        {
            sql.append( " AND dataset.\"name\" ILIKE :" + DISPLAY_NAME );
        }

        sql.append( " AND (" + sharingConditions( "dataset", paramsMap ) + ")" )
            .append( uidFiltering( "dataset", paramsMap ) )
            .append( " UNION " )
            .append(
                " SELECT dataset.uid, dataset.\"name\", dataset.code, dataset.\"name\" AS i18n_name" )
            .append( " FROM dataset" )
            .append( " WHERE (dataset.translations = '[]' OR dataset.translations IS NULL)" );

        if ( filterByDisplayName )
        {
            sql.append( " AND dataset.\"name\" ILIKE :" + DISPLAY_NAME );
        }

        sql.append( " AND (" + sharingConditions( "dataset", paramsMap ) + ")" )
            .append( uidFiltering( "dataset", paramsMap ) );

        return sql.toString();
    }
}
