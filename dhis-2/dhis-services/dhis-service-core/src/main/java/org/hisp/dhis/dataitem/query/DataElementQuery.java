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
import static org.hisp.dhis.common.DimensionItemType.DATA_ELEMENT;
import static org.hisp.dhis.common.ValueType.fromString;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.uidFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.valueTypeFiltering;
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
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataitem.DataItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * DataElements.
 *
 * @author maikel arabori
 */
@Component
public class DataElementQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DataElementQuery( @Qualifier( "readOnlyJdbcTemplate" )
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
            getDataElementQueryWith( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();
            final ValueType valueType = fromString( rowSet.getString( "valuetype" ) );
            final String name = trimToNull( rowSet.getString( "name" ) );
            final String displayName = defaultIfBlank( trimToNull( rowSet.getString( "i18n_name" ) ), name );

            viewItem.setName( name );
            viewItem.setDisplayName( displayName );
            viewItem.setValueType( valueType.name() );
            viewItem.setSimplifiedValueType( valueType.asSimplifiedValueType().name() );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( DATA_ELEMENT.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getDataElementQueryWith( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return DataElement.class;
    }

    private String getDataElementQueryWith( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append(
            "SELECT dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code" );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append( ", displayname.value AS i18n_name" );
        }
        else
        {
            sql.append( ", dataelement.\"name\" AS i18n_name" );
        }

        sql.append( " FROM dataelement " );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append(
                ", jsonb_to_recordset(dataelement.translations) as displayname(value TEXT, locale TEXT, property TEXT)" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "dataelement", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "dataelement", paramsMap ) );

        if ( hasStringPresence( paramsMap, DISPLAY_NAME ) )
        {
            if ( hasStringPresence( paramsMap, LOCALE ) )
            {
                final StringBuilder displayNameQuery = new StringBuilder();

                displayNameQuery
                    .append( " AND displayname.locale = :" + LOCALE )
                    .append( " AND displayname.property = 'NAME' AND displayname.value ILIKE :" + DISPLAY_NAME )
                    .append( " AND (" + sharingConditions( "dataelement", paramsMap ) + ")" )
                    .append( " UNION " )
                    .append(
                        " SELECT dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code, dataelement.\"name\" AS i18n_name" )
                    .append(
                        " FROM dataelement, jsonb_to_recordset(dataelement.translations) AS displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE dataelement.uid" )
                    .append( " NOT IN (" )
                    .append( " SELECT dataelement.uid FROM dataelement," )
                    .append(
                        " jsonb_to_recordset(dataelement.translations) AS displayname(locale TEXT, property TEXT)" )
                    .append( " WHERE displayname.locale = :" + LOCALE )
                    .append( ")" )
                    .append( " AND displayname.property = 'NAME'" )
                    .append( " AND dataelement.\"name\" ILIKE :" + DISPLAY_NAME )
                    .append( valueTypeFiltering( "dataelement", paramsMap ) )
                    .append( uidFiltering( "dataelement", paramsMap ) )
                    .append( " UNION " )
                    .append(
                        " SELECT dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code, dataelement.\"name\" AS i18n_name" )
                    .append( " FROM dataelement" )
                    .append( " WHERE (dataelement.translations = '[]' OR dataelement.translations IS NULL)" )
                    .append( " AND dataelement.\"name\" ILIKE :" + DISPLAY_NAME )
                    .append( valueTypeFiltering( "dataelement", paramsMap ) )
                    .append( uidFiltering( "dataelement", paramsMap ) )
                    .append( " AND (" + sharingConditions( "dataelement", paramsMap ) + ")" );

                sql.append( displayNameQuery.toString() );
            }
            else
            {
                // User does not have any locale set.
                sql.append( " AND ( dataelement.\"name\" ILIKE :" + DISPLAY_NAME + ")" );
            }
        }
        else if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            // If we reach here it means that we do not have a display name
            // filter set.
            // And the user has a default locale configured.
            final StringBuilder noDisplayNameQuery = new StringBuilder();

            noDisplayNameQuery
                .append( " AND displayname.locale = :" + LOCALE )
                .append( " AND displayname.property = 'NAME' " )
                // .append( " AND displayname.value ILIKE :" + DISPLAY_NAME )
                .append( " AND (" + sharingConditions( "dataelement", paramsMap ) + ")" )
                .append( " UNION " )
                .append(
                    " SELECT dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code, dataelement.\"name\" AS i18n_name" )
                .append(
                    " FROM dataelement, jsonb_to_recordset(dataelement.translations) AS displayname(locale TEXT, property TEXT)" )
                .append( " WHERE dataelement.uid" )
                .append( " NOT IN (" )
                .append( " SELECT dataelement.uid FROM dataelement," )
                .append(
                    " jsonb_to_recordset(dataelement.translations) AS displayname(locale TEXT, property TEXT)" )
                .append( " WHERE displayname.locale = :" + LOCALE )
                .append( ")" )
                .append( " AND displayname.property = 'NAME'" )
                // .append( " AND dataelement.\"name\" ILIKE :" + DISPLAY_NAME )
                .append( valueTypeFiltering( "dataelement", paramsMap ) )
                .append( uidFiltering( "dataelement", paramsMap ) )
                .append( " UNION " )
                .append(
                    " SELECT dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code, dataelement.\"name\" AS i18n_name" )
                .append( " FROM dataelement" )
                .append( " WHERE (dataelement.translations = '[]' OR dataelement.translations IS NULL)" )
                // .append( " AND dataelement.\"name\" ILIKE :" + DISPLAY_NAME )
                .append( valueTypeFiltering( "dataelement", paramsMap ) )
                .append( uidFiltering( "dataelement", paramsMap ) )
                .append( " AND (" + sharingConditions( "dataelement", paramsMap ) + ")" );

            sql.append( noDisplayNameQuery.toString() );
        }

        sql.append( valueTypeFiltering( "dataelement", paramsMap ) );
        sql.append( uidFiltering( "dataelement", paramsMap ) );

        sql.append(
            " GROUP BY dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code" );

        if ( hasStringPresence( paramsMap, LOCALE ) )
        {
            sql.append( ", i18n_name" );
        }

        if ( hasStringPresence( paramsMap, DISPLAY_NAME_ORDER ) )
        {
            if ( hasStringPresence( paramsMap, DISPLAY_NAME ) )
            {
                // 5 means i18n_name
                sql.append( displayColumnOrdering( 5, paramsMap ) );
            }
            else
            {
                // 2 means name
                sql.append( displayColumnOrdering( 2, paramsMap ) );
            }
        }
        else if ( hasStringPresence( paramsMap, NAME_ORDER ) )
        {
            sql.append( nameOrdering( "dataelement", paramsMap ) );
        }

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
