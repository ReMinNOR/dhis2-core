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
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_INDICATOR;
import static org.hisp.dhis.common.ValueType.NUMBER;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.skipValueType;
import static org.hisp.dhis.dataitem.query.shared.LimitStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.program.ProgramIndicator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * ProgramIndicators.
 *
 * @author maikel arabori
 */
@Component
public class ProgramIndicatorQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProgramIndicatorQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

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

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
            getProgramIndicatorQuery( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();

            viewItem.setName( rowSet.getString( "name" ) );
            viewItem.setDisplayName( defaultIfBlank( rowSet.getString( "pi_i18n_name" ), rowSet.getString( "name" ) ) );
            viewItem.setProgramId( rowSet.getString( "program_uid" ) );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( PROGRAM_INDICATOR.name() );

            // Specific case where we have to force a vale type. Program
            // Indicators don't have a value type but they always evaluate to
            // numbers.
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
        // It returns ZERO.
        if ( skipValueType( NUMBER, paramsMap ) )
        {
            return 0;
        }

        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getProgramIndicatorQuery( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return ProgramIndicator.class;
    }

    private String getProgramIndicatorQuery( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append(
            "SELECT programindicator.\"name\", programindicator.uid, programindicator.code, program.uid AS program_uid" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append( ", pi_displayname.value AS pi_i18n_name" );
        }

        sql.append( " FROM programindicator" )
            .append( " JOIN program ON program.programid = programindicator.programid" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append(
                " LEFT JOIN jsonb_to_recordset(program.translations) as p_displayname(value TEXT, locale TEXT, property TEXT) ON p_displayname.locale = :"
                    + LOCALE + " AND p_displayname.property = 'NAME'" );
            sql.append(
                " LEFT JOIN jsonb_to_recordset(programindicator.translations) as pi_displayname(value TEXT, locale TEXT, property TEXT) ON pi_displayname.locale = :"
                    + LOCALE + " AND pi_displayname.property = 'NAME'" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "programindicator", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "programindicator", paramsMap ) );

        sql.append( programIdFiltering( paramsMap ) );

        if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME )
            && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME ) ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( DISPLAY_NAME ),
                DISPLAY_NAME + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( DISPLAY_NAME ), DISPLAY_NAME + " cannot be null/blank." );

            if ( paramsMap.hasValue( LOCALE ) && paramsMap.hasValue( LOCALE )
                && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
            {
                isInstanceOf( String.class, paramsMap.getValue( LOCALE ),
                    LOCALE + " cannot be null and must be a String." );
                hasText( (String) paramsMap.getValue( LOCALE ), LOCALE + " cannot be null/blank." );

                sql.append( " AND (pi_displayname.value ILIKE :" + DISPLAY_NAME + ")" );

                sql.append( " UNION " )
                    .append(
                        " SELECT programindicator.\"name\", programindicator.uid, programindicator.code, program.uid AS program_uid, programindicator.\"name\" AS pi_i18n_name" )
                    .append( " FROM programindicator" )
                    .append( " JOIN program ON program.programid = programindicator.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(programindicator.translations) AS pi_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( " WHERE " )
                    .append( " programindicator.uid NOT IN (" )
                    .append( " SELECT programindicator.uid" )
                    .append( " FROM programindicator" )
                    .append( " JOIN program ON program.programid = programindicator.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(programindicator.translations) AS pi_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( "  WHERE" )
                    .append( " (pi_displayname.locale = :" + LOCALE + ")" )
                    .append( " )" )
                    .append( " AND (programindicator.name ILIKE :" + DISPLAY_NAME + ")" )
                    .append( " UNION " )
                    .append(
                        " SELECT programindicator.\"name\", programindicator.uid, programindicator.code, program.uid AS program_uid, programindicator.\"name\" as pi_i18n_name" )
                    .append( " FROM programindicator" )
                    .append( " JOIN program ON program.programid = programindicator.programid" )
                    .append( " WHERE" )
                    .append(
                        " (programindicator.translations = '[]' OR programindicator.translations IS NULL) AND programindicator.name ILIKE :"
                            + DISPLAY_NAME )
                    .append(
                        " GROUP BY programindicator.\"name\", programindicator.uid, program.uid, programindicator.code" );

                if ( paramsMap.hasValue( LOCALE ) && paramsMap.hasValue( LOCALE )
                    && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
                {
                    if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME_ORDER )
                        && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        final StringBuilder ordering = new StringBuilder();

                        if ( "ASC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 5 means pi_i18n_name
                            ordering.append( " ORDER BY 5 ASC" );
                        }
                        else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 5 means pi_i18n_name
                            ordering.append( " ORDER BY 5 DESC" );
                        }

                        sql.append( ordering.toString() );
                    }
                }
            }
            else
            {
                if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME_ORDER )
                    && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                {
                    final StringBuilder ordering = new StringBuilder();

                    if ( "ASC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        // 1 means programindicator."name"
                        ordering.append( " ORDER BY 1 ASC" );
                    }
                    else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        // 1 means programindicator."name"
                        ordering.append( " ORDER BY 1 DESC" );
                    }
                    // No locale, so we default the comparison to the raw name.
                    // In normal conditions this should never happen as every
                    // user/request should have a default locale.
                    sql.append(
                        " AND (program.\"name\" ILIKE :" + NAME + " OR programindicator.\"name\" ILIKE :" + NAME
                            + ")" );

                    sql.append( ordering.toString() );
                }
            }
        }
        else
        {
            sql.append(
                " GROUP BY program.\"name\", program.uid, programindicator.\"name\", programindicator.uid, programindicator.code, programindicator.translations, p_displayname.value, pi_displayname.value" );
        }

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }

    private String programIdFiltering( final MapSqlParameterSource paramsMap )
    {
        if ( paramsMap != null && paramsMap.hasValue( PROGRAM_ID ) )
        {
            isInstanceOf( String.class, paramsMap.getValue( PROGRAM_ID ),
                PROGRAM_ID + " cannot be null and must be a String." );
            hasText( (String) paramsMap.getValue( PROGRAM_ID ), PROGRAM_ID + " cannot be null/blank." );

            return " AND program.uid = :" + PROGRAM_ID;
        }

        return EMPTY;
    }
}
