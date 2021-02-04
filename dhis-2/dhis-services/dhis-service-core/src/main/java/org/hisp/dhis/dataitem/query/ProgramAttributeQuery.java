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
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_ATTRIBUTE;
import static org.hisp.dhis.common.ValueType.fromString;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.valueTypeFiltering;
import static org.hisp.dhis.dataitem.query.shared.LimitStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;
import static org.springframework.util.Assert.hasText;
import static org.springframework.util.Assert.isInstanceOf;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.program.ProgramTrackedEntityAttributeDimensionItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * ProgramTrackedEntityAttributeDimensionItems.
 *
 * @author maikel arabori
 */
@Component
public class ProgramAttributeQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProgramAttributeQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    public List<DataItem> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItem> dataItems = new ArrayList<>();

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet(
            getProgramAttributeQueryWith( paramsMap ), paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();
            final ValueType valueType = fromString( rowSet.getString( "valuetype" ) );

            viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
            viewItem.setDisplayName( defaultIfBlank( rowSet.getString( "p_i18n_name" ),
                rowSet.getString( "program_name" ) ) + SPACE
                + defaultIfBlank( rowSet.getString( "tea_i18n_name" ), rowSet.getString( "name" ) ) );
            viewItem.setValueType( valueType.name() );
            viewItem.setSimplifiedValueType( valueType.asSimplifiedValueType().name() );
            viewItem.setProgramId( rowSet.getString( "program_uid" ) );
            viewItem.setId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( PROGRAM_ATTRIBUTE.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getProgramAttributeQueryWith( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return ProgramTrackedEntityAttributeDimensionItem.class;
    }

    private String getProgramAttributeQueryWith( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append(
            "SELECT program.\"name\" AS program_name, program.uid AS program_uid, trackedentityattribute.\"name\", trackedentityattribute.uid," )
            .append( " trackedentityattribute.valuetype, trackedentityattribute.code" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append( ", p_displayname.value AS p_i18n_name" )
                .append( ", tea_displayname.value AS tea_i18n_name" );
        }

        sql.append( " FROM trackedentityattribute" )
            .append(
                " JOIN program_attributes ON program_attributes.trackedentityattributeid = trackedentityattribute.trackedentityattributeid" )
            .append( " JOIN program ON program_attributes.programid = program.programid" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append(
                " LEFT JOIN jsonb_to_recordset(program.translations) as p_displayname(value TEXT, locale TEXT, property TEXT) ON p_displayname.locale = :"
                    + LOCALE + " AND p_displayname.property = 'NAME'" );
            sql.append(
                " LEFT JOIN jsonb_to_recordset(trackedentityattribute.translations) as tea_displayname(value TEXT, locale TEXT, property TEXT) ON tea_displayname.locale = :"
                    + LOCALE + " AND tea_displayname.property = 'NAME'" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "program", "trackedentityattribute", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "program", "trackedentityattribute", paramsMap ) );

        sql.append( valueTypeFiltering( "trackedentityattribute", paramsMap ) );

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

                sql.append( " AND (tea_displayname.value ILIKE :" + DISPLAY_NAME + " OR p_displayname.value ILIKE  :"
                    + DISPLAY_NAME + ")" );

                sql.append( " UNION " )
                    .append(
                        " SELECT program.\"name\" AS program_name, program.uid AS program_uid," )
                    .append(
                        " trackedentityattribute.name, trackedentityattribute.\"uid\", trackedentityattribute.valuetype, trackedentityattribute.code," )
                    .append(
                        " program.\"name\" AS p_i18n_name, trackedentityattribute.\"name\" AS tea_i18n_name" )
                    .append( " FROM trackedentityattribute" )
                    .append(
                        " JOIN program_attributes ON program_attributes.trackedentityattributeid = trackedentityattribute.trackedentityattributeid" )
                    .append( " JOIN program ON program_attributes.programid = program.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(trackedentityattribute.translations) AS tea_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( " WHERE " )
                    .append( " trackedentityattribute.uid NOT IN (" )
                    .append( " SELECT trackedentityattribute.uid" )
                    .append( " FROM trackedentityattribute" )
                    .append(
                        " JOIN program_attributes ON program_attributes.trackedentityattributeid = trackedentityattribute.trackedentityattributeid" )
                    .append( " JOIN program ON program_attributes.programid = program.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(trackedentityattribute.translations) AS tea_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( "  WHERE" )
                    .append( " (tea_displayname.locale = :" + LOCALE + ")" )
                    .append( " OR" )
                    .append( " (p_displayname.locale = :" + LOCALE + ")" )
                    .append( " )" )
                    .append( " AND (trackedentityattribute.name ILIKE :" + DISPLAY_NAME + " OR program.name ILIKE :"
                        + DISPLAY_NAME
                        + ")" )
                    .append( valueTypeFiltering( "trackedentityattribute", paramsMap ) )
                    .append( programIdFiltering( paramsMap ) )
                    .append( " UNION " )
                    .append( " SELECT program.\"name\" AS program_name, program.uid AS program_uid," )
                    .append(
                        " trackedentityattribute.name, trackedentityattribute.\"uid\", trackedentityattribute.valuetype, trackedentityattribute.code," )
                    .append(
                        " program.\"name\" AS p_i18n_name, trackedentityattribute.\"name\" AS tea_i18n_name" )
                    .append( " FROM trackedentityattribute" )
                    .append(
                        " JOIN program_attributes ON program_attributes.trackedentityattributeid = trackedentityattribute.trackedentityattributeid" )
                    .append( " JOIN program ON program_attributes.programid = program.programid" )
                    .append( " WHERE" )
                    .append(
                        " (trackedentityattribute.translations = '[]' OR trackedentityattribute.translations IS NULL) AND trackedentityattribute.name ILIKE :"
                            + DISPLAY_NAME )
                    .append( " AND" )
                    .append(
                        " (program.translations = '[]' OR program.translations IS NULL) AND program.name ILIKE :"
                            + DISPLAY_NAME )
                    .append( valueTypeFiltering( "trackedentityattribute", paramsMap ) )
                    .append( programIdFiltering( paramsMap ) )
                    .append(
                        " GROUP BY program.\"name\", program.uid, trackedentityattribute.\"name\", trackedentityattribute.uid, trackedentityattribute.valuetype, trackedentityattribute.code" );

                if ( paramsMap.hasValue( LOCALE ) && paramsMap.hasValue( LOCALE )
                    && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
                {
                    if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME_ORDER )
                        && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        final StringBuilder ordering = new StringBuilder();

                        if ( "ASC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 7, 8 means p_i18n_name and tea_i18n_name
                            // respectively
                            ordering.append( " ORDER BY 7, 8 ASC" );
                        }
                        else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 7, 8 means p_i18n_name and tea_i18n_name
                            // respectively
                            ordering.append( " ORDER BY 7, 8 DESC" );
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
                        // 1, 3 means program."name" and
                        // trackedentityattribute."name"
                        // respectively
                        ordering.append( " ORDER BY 1, 3 ASC" );
                    }
                    else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        // 1, 3 means program."name" and
                        // trackedentityattribute."name"
                        // respectively
                        ordering.append( " ORDER BY 1, 3 DESC" );
                    }
                    // No locale, so we default the comparison to the raw name.
                    // In normal conditions this should never happen as every
                    // user/request should have a default locale.
                    sql.append(
                        " AND (program.\"name\" ILIKE :" + NAME + " OR trackedentityattribute.\"name\" ILIKE :" + NAME
                            + ")" );

                    sql.append( ordering.toString() );
                }
            }
        }
        else
        {
            sql.append(
                " GROUP BY program.\"name\", program.uid, trackedentityattribute.\"name\", trackedentityattribute.uid, trackedentityattribute.valuetype, trackedentityattribute.code, trackedentityattribute.translations, p_displayname.value, tea_displayname.value" );
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
