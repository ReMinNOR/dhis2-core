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
import static org.apache.commons.collections4.SetUtils.hashSet;
import static org.apache.commons.lang3.StringUtils.SPACE;
import static org.hisp.dhis.common.DimensionItemType.PROGRAM_DATA_ELEMENT;
import static org.hisp.dhis.common.JsonbConverter.fromJsonb;
import static org.hisp.dhis.dataitem.query.shared.CommonStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.commonFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.valueTypeFiltering;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.commonOrdering;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataitem.DataItem;
import org.hisp.dhis.program.ProgramDataElementDimensionItem;
import org.hisp.dhis.translation.Translation;
import org.postgresql.util.PGobject;
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
            final ValueType valueType = ValueType.fromString( rowSet.getString( "valuetype" ) );
            final Translation[] translations = fromJsonb( (PGobject) rowSet.getObject( "translations" ),
                Translation[].class );

            viewItem.setTranslations( hashSet( translations ) );
            viewItem.setName( rowSet.getString( "program_name" ) + SPACE + rowSet.getString( "name" ) );
            viewItem.setValueType( valueType.name() );
            viewItem.setSimplifiedValueType( valueType.asSimplifiedValueType().name() );
            viewItem.setCombinedId( rowSet.getString( "program_uid" ) + "." + rowSet.getString( "uid" ) );
            viewItem.setProgramId( rowSet.getString( "program_uid" ) );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setDimensionItemType( PROGRAM_DATA_ELEMENT.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT COUNT(DISTINCT (p.uid, de.uid))"
                + " FROM dataelement de"
                + " JOIN programstagedataelement psde ON psde.dataelementid = de.dataelementid"
                + " JOIN programstage ps ON psde.programstageid = ps.programstageid"
                + " JOIN program p ON p.programid = ps.programid"
                + " WHERE ("
                + sharingConditions( "p", "de", paramsMap )
                + ")" );

        sql.append( commonFiltering( "p", "de", paramsMap ) );

        if ( paramsMap.hasValue( VALUE_TYPES ) && paramsMap.getValue( VALUE_TYPES ) != null )
        {
            sql.append( " AND (de.valuetype IN (:" + VALUE_TYPES + "))" );
        }

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return ProgramDataElementDimensionItem.class;
    }

    private String getProgramDataElementQueryWith( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT p.\"name\" AS program_name, p.uid AS program_uid,"
                + " de.\"name\" AS name, de.uid AS uid, de.valuetype AS valuetype, de.translations"
                + " FROM dataelement de"
                + " JOIN programstagedataelement psde ON psde.dataelementid = de.dataelementid"
                + " JOIN programstage ps ON psde.programstageid = ps.programstageid"
                + " JOIN program p ON p.programid = ps.programid"
                + " WHERE ("
                + sharingConditions( "p", "de", paramsMap )
                + ")" );

        sql.append( commonFiltering( "p", "de", paramsMap ) );

        sql.append( valueTypeFiltering( "de", paramsMap ) );

        sql.append( " GROUP BY p.\"name\", p.uid, de.\"name\", de.uid, de.valuetype, de.translations" );

        sql.append( commonOrdering( "p", paramsMap ) );

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
