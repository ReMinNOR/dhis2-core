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
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_DATA_ELEMENT;
import static org.hisp.dhis.common.ValueType.fromString;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.nameFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.programIdFiltering;
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
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

/**
 * This component is responsible for providing query capabilities on top of
 * ProgramDataElementDimensionItems.
 *
 * @author maikel arabori
 */
@Component
public class ProgramDataElementDimensionQuery implements DataItemQuery
{
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ProgramDataElementDimensionQuery( @Qualifier( "readOnlyJdbcTemplate" )
    final JdbcTemplate jdbcTemplate )
    {
        checkNotNull( jdbcTemplate );

        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate( jdbcTemplate );
    }

    public List<DataItem> find( final MapSqlParameterSource paramsMap )
    {
        final List<DataItem> dataItems = new ArrayList<>();

        final SqlRowSet rowSet = namedParameterJdbcTemplate.queryForRowSet( getProgramDataElementQueryWith( paramsMap ),
            paramsMap );

        while ( rowSet.next() )
        {
            final DataItem viewItem = new DataItem();
            final ValueType valueType = fromString( rowSet.getString( "valuetype" ) );

            viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
            viewItem.setDisplayName( defaultIfBlank( rowSet.getString( "p_i18n_name" ),
                rowSet.getString( "program_name" ) ) + SPACE
                + defaultIfBlank( rowSet.getString( "de_i18n_name" ), rowSet.getString( "name" ) ) );
            viewItem.setValueType( valueType.name() );
            viewItem.setSimplifiedValueType( valueType.asSimplifiedValueType().name() );
            viewItem.setProgramId( rowSet.getString( "program_uid" ) );
            viewItem.setId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
            viewItem.setCode( rowSet.getString( "code" ) );
            viewItem.setDimensionItemType( PROGRAM_DATA_ELEMENT.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append( "SELECT COUNT(*) FROM (" )
            .append( getProgramDataElementQueryWith( paramsMap ).replace( maxLimit( paramsMap ), EMPTY ) )
            .append( ") t" );

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return ProgramDataElementDimensionItem.class;
    }

    private String getProgramDataElementQueryWith( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder();

        sql.append(
            "SELECT program.\"name\" AS program_name, program.uid AS program_uid, dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append( ", p_displayname.value AS p_i18n_name" )
                .append( ", de_displayname.value AS de_i18n_name" );
        }

        sql.append( " FROM dataelement" )
            .append(
                " JOIN programstagedataelement ON programstagedataelement.dataelementid = dataelement.dataelementid" )
            .append( " JOIN programstage ON programstagedataelement.programstageid = programstage.programstageid" )
            .append( " JOIN program ON program.programid = programstage.programid" );

        if ( paramsMap != null && paramsMap.hasValue( LOCALE ) && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
        {
            sql.append(
                " LEFT JOIN jsonb_to_recordset(program.translations) as p_displayname(value TEXT, locale TEXT, property TEXT) ON p_displayname.locale = :"
                    + LOCALE + " AND p_displayname.property = 'NAME'" );
            sql.append(
                " LEFT JOIN jsonb_to_recordset(dataelement.translations) as de_displayname(value TEXT, locale TEXT, property TEXT) ON de_displayname.locale = :"
                    + LOCALE + " AND de_displayname.property = 'NAME'" );
        }

        sql.append( " WHERE (" )
            .append( sharingConditions( "program", "dataelement", paramsMap ) )
            .append( ")" );

        sql.append( nameFiltering( "program", "dataelement", paramsMap ) );

        sql.append( valueTypeFiltering( "dataelement", paramsMap ) );

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

                sql.append( " AND (de_displayname.value ILIKE :" + DISPLAY_NAME + " OR p_displayname.value ILIKE  :"
                    + DISPLAY_NAME + ")" );

                sql.append( " UNION " )
                    .append(
                        " SELECT program.\"name\" AS program_name, program.uid AS program_uid," )
                    .append( " dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code," )
                    .append( " program.\"name\" AS p_i18n_name, dataelement.\"name\" AS de_i18n_name" )
                    .append( " FROM dataelement" )
                    .append(
                        " JOIN programstagedataelement ON programstagedataelement.dataelementid = dataelement.dataelementid" )
                    .append(
                        " JOIN programstage ON programstagedataelement.programstageid = programstage.programstageid" )
                    .append( " JOIN program ON program.programid = programstage.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS de_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(dataelement.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( " WHERE " )
                    .append( " dataelement.uid NOT IN (" )
                    .append( " SELECT dataelement.uid" )
                    .append( " FROM dataelement" )
                    .append(
                        " JOIN programstagedataelement ON programstagedataelement.dataelementid = dataelement.dataelementid" )
                    .append(
                        " JOIN programstage ON programstagedataelement.programstageid = programstage.programstageid" )
                    .append( " JOIN program ON program.programid = programstage.programid" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(program.translations) AS de_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append(
                        " LEFT JOIN jsonb_to_recordset(dataelement.translations) AS p_displayname(value TEXT, locale TEXT, property TEXT) ON TRUE" )
                    .append( "  WHERE" )
                    .append( " (de_displayname.locale = :" + LOCALE + ")" )
                    .append( " OR" )
                    .append( " (p_displayname.locale = :" + LOCALE + ")" )
                    .append( " )" )
                    .append( " AND (dataelement.name ILIKE :" + DISPLAY_NAME + " OR program.name ILIKE :" + DISPLAY_NAME
                        + ")" )
                    .append( valueTypeFiltering( "dataelement", paramsMap ) )
                    .append( programIdFiltering( paramsMap ) )
                    .append( " UNION " )
                    .append(
                        " SELECT program.\"name\" AS program_name, program.uid AS program_uid," )
                    .append( " dataelement.uid, dataelement.\"name\", dataelement.valuetype, dataelement.code," )
                    .append( " program.\"name\" AS p_i18n_name, dataelement.\"name\" AS de_i18n_name" )
                    .append( " FROM dataelement" )
                    .append(
                        " JOIN programstagedataelement ON programstagedataelement.dataelementid = dataelement.dataelementid" )
                    .append(
                        " JOIN programstage ON programstagedataelement.programstageid = programstage.programstageid" )
                    .append( " JOIN program ON program.programid = programstage.programid" )
                    .append( " WHERE" )
                    .append(
                        " (dataelement.translations = '[]' OR dataelement.translations IS NULL) AND dataelement.name ILIKE :"
                            + DISPLAY_NAME )
                    .append( " AND" )
                    .append(
                        " (program.translations = '[]' OR program.translations IS NULL) AND program.name ILIKE :"
                            + DISPLAY_NAME )
                    .append( valueTypeFiltering( "dataelement", paramsMap ) )
                    .append( programIdFiltering( paramsMap ) )
                    .append(
                        " GROUP BY program.\"name\", program.uid, dataelement.\"name\", dataelement.uid, dataelement.valuetype, dataelement.code" );

                if ( paramsMap.hasValue( LOCALE ) && paramsMap.hasValue( LOCALE )
                    && isNotBlank( (String) paramsMap.getValue( LOCALE ) ) )
                {
                    if ( paramsMap != null && paramsMap.hasValue( DISPLAY_NAME_ORDER )
                        && isNotBlank( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        final StringBuilder ordering = new StringBuilder();

                        if ( "ASC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 7, 8 means p_i18n_name and de_i18n_name
                            // respectively
                            ordering.append( " ORDER BY 7, 8 ASC" );
                        }
                        else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                        {
                            // 7, 8 means p_i18n_name and de_i18n_name
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
                        // 1, 4 means program."name" and dataelement."name"
                        // respectively
                        ordering.append( " ORDER BY 1, 4 ASC" );
                    }
                    else if ( "DESC".equalsIgnoreCase( (String) paramsMap.getValue( DISPLAY_NAME_ORDER ) ) )
                    {
                        // 1, 4 means program."name" and dataelement."name"
                        // respectively
                        ordering.append( " ORDER BY 1, 4 DESC" );
                    }
                    // No locale, so we default the comparison to the raw name.
                    // In normal conditions this should never happen as every
                    // user/request should have a default locale.
                    sql.append(
                        " AND (program.\"name\" ILIKE :" + NAME + " OR dataelement.\"name\" ILIKE :" + NAME + ")" );

                    sql.append( ordering.toString() );
                }
            }
        }
        else
        {
            sql.append(
                " GROUP BY program.\"name\", program.uid, dataelement.\"name\", dataelement.uid, dataelement.valuetype, dataelement.code, dataelement.translations, p_displayname.value, de_displayname.value" );
        }

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
