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
import static org.hisp.dhis.common.DimensionItemType.DATA_ELEMENT;
import static org.hisp.dhis.common.JsonbConverter.fromJsonb;
import static org.hisp.dhis.common.ValueType.fromString;
import static org.hisp.dhis.dataitem.query.shared.CommonStatement.maxLimit;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.commonFiltering;
import static org.hisp.dhis.dataitem.query.shared.FilteringStatement.valueTypeFiltering;
import static org.hisp.dhis.dataitem.query.shared.OrderingStatement.commonOrdering;
import static org.hisp.dhis.dataitem.query.shared.UserAccessStatement.sharingConditions;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseDimensionalItemObject;
import org.hisp.dhis.common.ValueType;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataitem.DataItem;
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
            final Translation[] translations = fromJsonb( (PGobject) rowSet.getObject( "translations" ),
                Translation[].class );

            viewItem.setTranslations( hashSet( translations ) );
            viewItem.setName( rowSet.getString( "name" ) );
            viewItem.setValueType( valueType.name() );
            viewItem.setSimplifiedValueType( valueType.asSimplifiedValueType().name() );
            viewItem.setId( rowSet.getString( "uid" ) );
            viewItem.setDimensionItemType( DATA_ELEMENT.name() );

            dataItems.add( viewItem );
        }

        return dataItems;
    }

    @Override
    public int count( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT COUNT(DISTINCT de.uid)"
                + " FROM dataelement de"
                + " WHERE ("
                + sharingConditions( "de", paramsMap )
                + ")" );

        sql.append( commonFiltering( "de", paramsMap ) );

        if ( paramsMap.hasValue( VALUE_TYPES ) && paramsMap.getValue( VALUE_TYPES ) != null )
        {
            sql.append( " AND (de.valuetype IN (:" + VALUE_TYPES + "))" );
        }

        return namedParameterJdbcTemplate.queryForObject( sql.toString(), paramsMap, Integer.class );
    }

    @Override
    public Class<? extends BaseDimensionalItemObject> getAssociatedEntity()
    {
        return DataElement.class;
    }

    private String getDataElementQueryWith( final MapSqlParameterSource paramsMap )
    {
        final StringBuilder sql = new StringBuilder(
            "SELECT de.\"name\" AS name, de.uid AS uid, de.valuetype AS valuetype, de.translations"
                + " FROM dataelement de"
                + " WHERE ("
                + sharingConditions( "de", paramsMap )
                + ")" );

        sql.append( commonFiltering( "de", paramsMap ) );

        sql.append( valueTypeFiltering( "de", paramsMap ) );

        sql.append( commonOrdering( "de", paramsMap ) );

        sql.append( maxLimit( paramsMap ) );

        return sql.toString();
    }
}
